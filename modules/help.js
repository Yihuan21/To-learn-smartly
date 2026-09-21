const modal = document.getElementById("modal");
const modalTitle = document.getElementById("modalTitle");
const modalBody = document.getElementById("modalBody");
const closeModal = document.getElementById("closeModal");


function showModal(title, body){

    modal.classList.remove("hidden");

    modalTitle.textContent = title;

    modalBody.innerHTML = body;
}


closeModal.onclick = ()=>{

    modal.classList.add("hidden");

};



function showHelp(){

    showModal(
        "📘 使用帮助",

`
<h3>1. 上传课程表</h3>
<p>
点击“上传课程表图片”，选择课程表照片。
</p>

<h3>2. 自动整理</h3>
<p>
系统会分析课程内容并生成课程卡片。
</p>

<h3>3. 修改课程</h3>
<p>
点击课程卡片，可以修改课程信息。
</p>

<ul>
<li>课程名称</li>
<li>上课时间</li>
<li>教室</li>
<li>颜色</li>
</ul>

<h3>4. 保存</h3>
<p>
课程会保存在本地浏览器。
</p>
`
    );

}
