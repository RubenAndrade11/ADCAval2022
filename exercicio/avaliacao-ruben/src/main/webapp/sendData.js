function sendRegister() {

    var xmlHttpReq = new XMLHttpRequest();

    if (xmlHttpReq){
        xmlHttpReq.onreadystatechange = function() {
            if (xmlHttpReq.readyState === 4 && xmlHttpReq.status === 200) {
                const obj = JSON.parse(xmlHttpReq.responseText);

                console.log(xmlHttpReq.responseText);

                for (let i = 0 ; i < obj.length ; i++){
                console.log(obj[i]);
                }
            }
           }
    }

    xmlHttpReq.open("POST", "https://avaliacao-ruben.appspot.com/rest/register/user");

    xmlHttpReq.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

    xmlHttpReq.send(JSON.stringify({
        username: new String(document.getElementById("uname").value),
        email: new String(document.getElementById("email").value),
        name: new String(document.getElementById("name").value),
        password: new String(document.getElementById("pword").value),
        mobilephone: new String(document.getElementById("tmovel").value),
        phone: new String(document.getElementById("tfixo").value),
        address: new String(document.getElementById("morada").value),
        nif: new String(document.getElementById("nif").value),
        privacy: new String(document.getElementById("privacy").value)
    }));
}