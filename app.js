let courses=loadCourses();

function render(){
const box=document.getElementById("schedule");
box.innerHTML="";
courses.forEach((c,i)=>{
let div=document.createElement("div");
div.className="course";
div.style.background=c.color||"white";
div.innerHTML=`<b>${c.title}</b><br>
${c.day} ${c.start}-${c.end}<br>
${c.room||""}`;
div.onclick=()=>editCourse(i,courses,render);
box.appendChild(div);
});
}

document.getElementById("helpBtn").onclick=showHelp;

document.getElementById("uploadBtn").onclick=()=>{
document.getElementById("imageInput").click();
};

document.getElementById("imageInput").onchange=async(e)=>{
showModal("处理中","正在整理课程，请稍候");
};

render();
