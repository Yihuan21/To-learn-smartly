alert("help.js新版加载成功");
// modules/help.js
// 新手帮助模块

function showModal(title, body){

    const modal = document.getElementById("modal");
    const modalTitle = document.getElementById("modalTitle");
    const modalBody = document.getElementById("modalBody");

    if(!modal || !modalTitle || !modalBody){
        console.error("帮助弹窗元素不存在");
        return;
    }

    modal.classList.remove("hidden");

    modalTitle.textContent = title;

    modalBody.innerHTML = body;
}


// 关闭按钮

const closeModalBtn = document.getElementById("closeModal");

if(closeModalBtn){

    closeModalBtn.onclick = function(){

        const modal = document.getElementById("modal");

        if(modal){
            modal.classList.add("hidden");
        }

    };

}



// 新手帮助

function showHelp(){

    showModal(

        "📘 新手使用指南",

`
<div class="help-content">

<h3>欢迎使用智能课程表</h3>

<p>
这里可以帮助你整理课程、修改安排，并逐步优化学习计划。
</p>


<h3>① 上传课程表</h3>

<p>
点击「上传课程表图片」，
选择清晰的课程表照片。
</p>


<h3>② 自动整理</h3>

<p>
系统会帮助你整理：
</p>

<ul>
<li>课程名称</li>
<li>星期</li>
<li>上课时间</li>
<li>课程信息</li>
</ul>


<h3>③ 修改课程</h3>

<p>
点击课程卡片后，可以调整：
</p>

<ul>
<li>课程名称</li>
<li>时间</li>
<li>颜色</li>
<li>其他备注</li>
</ul>


<h3>④ 保存</h3>

<p>
修改后的课程会自动保存到本设备。
</p>


<h3>⑤ 使用建议</h3>

<p>
第一次使用建议：
</p>

<ul>
<li>上传完整课程表</li>
<li>确认识别结果</li>
<li>再进行修改</li>
</ul>


<p>
祝你学习顺利 🌱
</p>

</div>
`

    );

}



// 暴露给 app.js

window.showHelp = showHelp;
