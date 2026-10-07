# ============================================================
#  Smart Property Management System - smoke test
#  Logs in as each role and requests every page, reporting
#  HTTP status / length and whether an error page was rendered.
#  Usage:  powershell -ExecutionPolicy Bypass -File scripts\verify-pages.ps1
#  Note: keep this file ASCII-only (Windows PowerShell 5.1 reads
#        .ps1 files using the system codepage).
# ============================================================
param(
    [string]$BaseUrl = "http://localhost:8888/property-ms"
)

$ErrorActionPreference = "Continue"

$accounts = @(
    @{ user = "admin";    pass = "123456"; role = "ADMIN" },
    @{ user = "wuye01";   pass = "123456"; role = "STAFF" },
    @{ user = "zhangsan"; pass = "123456"; role = "OWNER" }
)

# page => roles that should be able to open it
$pages = @(
    @{ path = "/dashboard";      roles = @("ADMIN","STAFF","OWNER") },
    @{ path = "/building/list";  roles = @("ADMIN","STAFF") },
    @{ path = "/room/list";      roles = @("ADMIN","STAFF") },
    @{ path = "/owner/list";     roles = @("ADMIN","STAFF") },
    @{ path = "/owner/my";       roles = @("OWNER") },
    @{ path = "/fee/list";       roles = @("ADMIN","STAFF","OWNER") },
    @{ path = "/repair/list";    roles = @("ADMIN","STAFF","OWNER") },
    @{ path = "/complaint/list"; roles = @("ADMIN","STAFF","OWNER") },
    @{ path = "/notice/list";    roles = @("ADMIN","STAFF","OWNER") },
    @{ path = "/visitor/list";   roles = @("ADMIN","STAFF") },
    @{ path = "/parking/list";   roles = @("ADMIN","STAFF") },
    @{ path = "/user/list";      roles = @("ADMIN") },
    @{ path = "/profile";        roles = @("ADMIN","STAFF","OWNER") },
    @{ path = "/repair/detail?id=1";    roles = @("ADMIN","STAFF","OWNER") },
    @{ path = "/complaint/detail?id=1"; roles = @("ADMIN","STAFF","OWNER") },
    @{ path = "/notice/detail?id=1";    roles = @("ADMIN","STAFF","OWNER") }
)

$fail = 0

foreach ($acc in $accounts) {
    Write-Output ("=========== login as {0} ({1}) ===========" -f $acc.user, $acc.role)
    try {
        $null = Invoke-WebRequest -Uri "$BaseUrl/doLogin" -Method POST `
            -Body @{ username = $acc.user; password = $acc.pass } `
            -SessionVariable sess -MaximumRedirection 0 -UseBasicParsing -ErrorAction Stop
        Write-Output "  [WARN] login did not redirect (unexpected)"
    } catch {
        $code = $_.Exception.Response.StatusCode.value__
        if ($code -eq 302) {
            Write-Output "  login OK (302 redirect)"
        } else {
            Write-Output "  [FAIL] login returned $code"
            $fail++
            continue
        }
    }

    # 完整链路检查：登录 -> 根路径 "/" -> 首页概览，确认浏览器跟进重定向后不会出现 404
    # 这里必须使用全新会话，避免上一轮 -MaximumRedirection 0 的影响
    try {
        $landing = Invoke-WebRequest -Uri "$BaseUrl/doLogin" -Method POST `
            -Body @{ username = $acc.user; password = $acc.pass } `
            -SessionVariable chainSess -UseBasicParsing -TimeoutSec 30
        if ($landing.StatusCode -eq 200 -and $landing.Content -notmatch 'error-code' `
                -and $landing.Content.Length -gt 1000) {
            Write-Output ("  login redirect chain OK (landed on page, {0} bytes)" -f $landing.Content.Length)
        } else {
            Write-Output "  [FAIL] login redirect chain ended on an error page"
            $fail++
        }
    } catch {
        Write-Output ("  [FAIL] login redirect chain: {0}" -f $_.Exception.Message)
        $fail++
    }

    foreach ($p in $pages) {
        if ($p.roles -notcontains $acc.role) { continue }
        try {
            $r = Invoke-WebRequest -Uri ($BaseUrl + $p.path) -WebSession $sess `
                -UseBasicParsing -TimeoutSec 30
            $isErrorPage = $r.Content -match 'error-code'
            # 业主端页面统一使用顶部导航，若缺失说明模板漏了导航片段
            $missingNav = ($acc.role -eq "OWNER") -and ($r.Content -notmatch 'topnav')
            $status = "OK "
            if ($isErrorPage -or $missingNav) { $status = "ERR"; $fail++ }
            $note = ""
            if ($missingNav) { $note = "  <= 缺少顶部导航" }
            Write-Output ("  [{0}] {1,-22} http={2} bytes={3}{4}" -f `
                $status, $p.path, $r.StatusCode, $r.Content.Length, $note)
        } catch {
            Write-Output ("  [ERR] {0,-22} {1}" -f $p.path, $_.Exception.Message)
            $fail++
        }
    }
}

Write-Output "==========================================="
if ($fail -eq 0) {
    Write-Output "ALL PAGES OK"
} else {
    Write-Output ("FAILED CHECKS: {0}" -f $fail)
}
