function showModal(title,body){
modal.classList.remove("hidden");
modalTitle.textContent=title;
modalBody.textContent=body;
}
closeModal.onclick=()=>modal.classList.add("hidden");

function showHelp(){
showModal(
"新手使用方法",
"上传课程表图片后，系统会自动整理课程。点击课程卡片可以修改内容、时间和颜色。"
);
}
