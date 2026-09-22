// modules/help.js
// 新手帮助与通用弹窗模块


// ==============================
// 通用弹窗
// ==============================

function showModal(title, body) {

  const modal = document.getElementById("modal");
  const modalTitle = document.getElementById("modalTitle");
  const modalBody = document.getElementById("modalBody");

  if (!modal) {
    console.error("找不到 #modal");
    return;
  }

  if (modalTitle) {
    modalTitle.textContent = title || "";
  }

  if (modalBody) {
    modalBody.innerHTML = body || "";
  }

  modal.classList.remove("hidden");
}


// ==============================
// 关闭弹窗
// ==============================

function closeHelpModal() {

  const modal = document.getElementById("modal");

  if (modal) {
    modal.classList.add("hidden");
  }
}


// ==============================
// 新手使用指南
// ==============================

function showHelp() {

  showModal(
    "📘 新手使用指南",
    `
    <h3>欢迎使用智能课程表</h3>

    <p>
      这是一个帮助你整理课程、查看课程安排的小工具。
    </p>

    <h3>① 上传课程表</h3>

    <p>
      点击首页的「上传课程表」按钮，
      选择一张清晰完整的课程表图片。
    </p>

    <h3>② 查看课程</h3>

    <p>
      系统整理完成后，课程会显示在课程表中。
    </p>

    <h3>③ 修改课程</h3>

    <p>
      点击课程卡片，可以修改课程信息。
    </p>

    <ul>
      <li>课程名称</li>
      <li>时间</li>
      <li>颜色</li>
    </ul>

    <h3>④ 保存</h3>

    <p>
      修改课程后会自动保存。
    </p>

    <h3>⑤ 如果第一次使用</h3>

    <p>
      建议先上传一张完整、清晰的课程表图片，
      再根据整理结果进行修改。
    </p>

    <p>
      你也可以随时点击右上角的「？」重新查看本指南。
    </p>

    <p>
      祝你学习顺利 🌱
    </p>
    `
  );
}


// ==============================
// 暴露给 app.js
// ==============================

window.showModal = showModal;
window.showHelp = showHelp;
window.closeHelpModal = closeHelpModal;


// ==============================
// 绑定关闭按钮
// ==============================

function setupHelpModal() {

  const closeModal = document.getElementById("closeModal");

  if (closeModal) {

    closeModal.onclick = function() {
      closeHelpModal();
    };

  }
}


// 如果页面已经加载完成，直接绑定。
// 如果还没有加载完成，等 DOM 加载完成后再绑定。

if (document.readyState === "loading") {

  document.addEventListener(
    "DOMContentLoaded",
    setupHelpModal
  );

} else {

  setupHelpModal();

}
