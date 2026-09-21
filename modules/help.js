alert("help.js新版加载成功");
// modules/help.js
// 新手帮助模块

// modules/help.js

window.addEventListener("DOMContentLoaded", ()=>{


    const modal = document.getElementById("modal");
    const modalTitle = document.getElementById("modalTitle");
    const modalBody = document.getElementById("modalBody");
    const closeModal = document.getElementById("closeModal");


    if(closeModal){

        closeModal.onclick = ()=>{

            modal.classList.add("hidden");

        };

    }



    window.showHelp = function(){


        if(!modal){
            console.error("找不到帮助弹窗");
            return;
        }


        modal.classList.remove("hidden");


        modalTitle.textContent="📘 新手使用指南";


        modalBody.innerHTML=`

<h3>欢迎使用智能课程表</h3>

<p>
上传课程表图片后，系统会帮助整理课程。
</p>


<h3>① 上传图片</h3>

<p>
选择清晰完整的课程表照片。
</p>


<h3>② 修改课程</h3>

<p>
点击课程卡片，可以修改：
</p>

<ul>
<li>课程名称</li>
<li>时间</li>
<li>颜色</li>
</ul>


<h3>③ 保存</h3>

<p>
修改会自动保存。
</p>


<p>
祝学习顺利 🌱
</p>

`;

    };


});

// 暴露给 app.js

window.showHelp = showHelp;
