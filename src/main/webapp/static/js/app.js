/* ==========================================================
   智慧物业社区管理系统 - 公共脚本
   ========================================================== */

/**
 * 打开新增表单
 * @param {string} formId  表单元素 id
 * @param {string} panelId 表单面板 id
 * @param {string} title   面板标题
 */
function openCreate(formId, panelId, title) {
    var form = document.getElementById(formId);
    if (!form) return;
    form.reset();
    var idField = form.elements['id'];
    if (idField) idField.value = '';
    setPanelTitle(panelId, title || '新增记录');
    showPanel(panelId);
}

/**
 * 打开编辑表单：把按钮上的 data-* 属性回填到表单中
 * @param {HTMLElement} btn 触发按钮
 * @param {string} formId   表单元素 id
 * @param {string} panelId  表单面板 id
 * @param {string} title    面板标题
 */
function openEdit(btn, formId, panelId, title) {
    var form = document.getElementById(formId);
    if (!form) return;
    form.reset();
    fillForm(form, btn.dataset);
    setPanelTitle(panelId, title || '编辑记录');
    showPanel(panelId);
}

/**
 * 将 data-* 数据按字段名（忽略大小写）回填到表单控件
 */
function fillForm(form, dataset) {
    var map = {};
    for (var i = 0; i < form.elements.length; i++) {
        var el = form.elements[i];
        if (el.name) map[el.name.toLowerCase()] = el;
    }
    for (var key in dataset) {
        if (!Object.prototype.hasOwnProperty.call(dataset, key)) continue;
        var target = map[key.toLowerCase()];
        if (!target) continue;
        var value = dataset[key];
        if (value === 'null' || value === 'undefined') value = '';
        target.value = value;
    }
}

function showPanel(panelId) {
    var panel = document.getElementById(panelId);
    if (!panel) return;
    panel.classList.add('open');
    document.body.classList.add('modal-open');
    var firstInput = panel.querySelector('input:not([type=hidden]), select, textarea');
    if (firstInput) {
        window.setTimeout(function () { firstInput.focus(); }, 60);
    }
}

function closePanel(panelId) {
    var panel = document.getElementById(panelId);
    if (panel) panel.classList.remove('open');
    syncModalState();
}

function setPanelTitle(panelId, title) {
    var panel = document.getElementById(panelId);
    if (!panel) return;
    var el = panel.querySelector('.panel-title');
    if (el) el.textContent = title;
}

/** 页面中是否还有打开的弹窗，用于控制遮罩层与 body 滚动 */
function syncModalState() {
    var opened = document.querySelectorAll('.form-panel.open');
    if (opened.length > 0) {
        document.body.classList.add('modal-open');
    } else {
        document.body.classList.remove('modal-open');
    }
}

/** 删除确认 */
function confirmSubmit(message) {
    return confirm(message || '确定要执行该操作吗？');
}

/** 表格行内快捷筛选 */
function filterTable(inputId, tableId) {
    var keyword = document.getElementById(inputId).value.trim().toLowerCase();
    var rows = document.getElementById(tableId).querySelectorAll('tbody tr');
    for (var i = 0; i < rows.length; i++) {
        var text = rows[i].innerText.toLowerCase();
        rows[i].style.display = (keyword === '' || text.indexOf(keyword) > -1) ? '' : 'none';
    }
}

/* ==========================================================
   初始化：把表单面板改造成弹窗（遮罩层 + 右上角关闭按钮）
   ========================================================== */
(function initModalPanels() {
    function ready() {
        // 遮罩层
        var backdrop = document.createElement('div');
        backdrop.className = 'modal-backdrop';
        backdrop.addEventListener('click', function () {
            var opened = document.querySelectorAll('.form-panel.open');
            for (var i = 0; i < opened.length; i++) {
                opened[i].classList.remove('open');
            }
            syncModalState();
        });
        document.body.appendChild(backdrop);

        // 每个表单面板右上角补一个关闭按钮
        var panels = document.querySelectorAll('.form-panel');
        for (var i = 0; i < panels.length; i++) {
            (function (panel) {
                var close = document.createElement('button');
                close.type = 'button';
                close.className = 'modal-close';
                close.innerHTML = '&times;';
                close.setAttribute('aria-label', '关闭');
                close.addEventListener('click', function () {
                    panel.classList.remove('open');
                    syncModalState();
                });
                panel.appendChild(close);
            })(panels[i]);
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', ready);
    } else {
        ready();
    }
})();

/** 顶部提示条自动淡出 */
window.setTimeout(function () {
    var alerts = document.querySelectorAll('.alert-auto');
    for (var i = 0; i < alerts.length; i++) {
        alerts[i].style.transition = 'opacity .5s';
        alerts[i].style.opacity = '0';
        (function (el) {
            window.setTimeout(function () { el.style.display = 'none'; }, 520);
        })(alerts[i]);
    }
}, 3200);
