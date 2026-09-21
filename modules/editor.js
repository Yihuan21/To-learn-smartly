function editCourse(i,courses,render){
let c=courses[i];
let name=prompt("课程名称",c.title);
if(name){
c.title=name;
saveCourses(courses);
render();
}
}
