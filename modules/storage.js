function loadCourses(){
return JSON.parse(localStorage.getItem("courses")||"[]");
}
function saveCourses(data){
localStorage.setItem("courses",JSON.stringify(data));
}
