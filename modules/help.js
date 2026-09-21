function showModal(title,body){

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
点击“上传课程表图片”，选择手机或iPad中的课程表照片。
</p>


<h3>2. 自动整理</h3>
<p>
系统会读取图片内容，并生成课程卡片。
</p>


<h3>3. 修改课程</h3>
<p>
点击任意课程卡片，可以修改：
</p>

<ul>
<li>课程名称</li>
<li>星期</li>
<li>时间</li>
<li>教室</li>
<li>颜色</li>
</ul>


<h3>4. 数据保存</h3>
<p>
课程数据会保存在浏览器本地，下次打开仍然可以看到。
</p>


<h3>5. iPad使用建议</h3>
<p>
建议使用Chrome或Safari浏览。
添加到主屏幕后，可以像App一样使用。
</p>


<h3>6. 后续功能</h3>
<p>
未来版本将支持：
</p>

<ul>
<li>AI识别课程表</li>
<li>课程搜索</li>
<li>考试提醒</li>
<li>学习计划生成</li>
</ul>

`
);

}
