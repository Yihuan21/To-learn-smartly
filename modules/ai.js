async function callSmartService(path,data){
return fetch(
"https://schedule-ai-proxy.yihuanchen219.workers.dev"+path,
{
method:"POST",
headers:{"Content-Type":"application/json"},
body:JSON.stringify(data)
});
}
