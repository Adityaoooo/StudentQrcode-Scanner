<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Scan Student QR Code</title>
    <script src="https://unpkg.com/html5-qrcode"></script>
    <style>
        body { font-family: Arial; margin:20px; background:#f4f4f4; }
        .card { background:#fff; border-radius:8px; padding:20px; width:300px; margin-top:20px; box-shadow:0 2px 8px rgba(0,0,0,0.2);}
        .card img { display:block; margin:10px auto; border-radius:8px;}
        #reader { margin-bottom:20px;}
        .btn { background:#007bff; color:#fff; padding:8px 12px; border:none; border-radius:6px; cursor:pointer; margin-top:10px;}
        .btn:hover { background:#0056b3; }
    </style>
</head>
<body>
    <h2>📷 Scan Student QR Code</h2>
    <div id="reader" style="width:300px;"></div>
    <input type="file" accept="image/*" onchange="scanFromFile(this)" class="btn"/>
    <div id="result"></div>

    <script>
        function fetchStudentInfo(qrValue){
            fetch('<%= request.getContextPath() %>/getStudentInfo?qr=' + encodeURIComponent(qrValue))
                .then(res => res.json())
                .then(data => displayStudentData(data))
                .catch(err => document.getElementById('result').innerHTML = '<b style="color:red">Error:</b> '+err);
        }

        function displayStudentData(data){
            if(data.error){
                document.getElementById('result').innerHTML = '<b style="color:red">Error:</b> ' + data.error;
            } else {
                document.getElementById('result').innerHTML =
                    `<div class="card">
                        <h3>${data.name}</h3>
                        <p><b>Gender:</b> ${data.gender}</p>
                        <p><b>Course:</b> ${data.course}</p>
                        <p><b>DOB:</b> ${data.dob}</p>
                        <img src="${data.photoBase64 ? 'data:image/png;base64,'+data.photoBase64 : 'default.png'}" width="150"/>
                     </div>`;
            }
        }

        // Live camera QR scanner
        var html5QrcodeScanner = new Html5QrcodeScanner("reader", { fps:10, qrbox:250 });
        html5QrcodeScanner.render(decodedText => fetchStudentInfo(decodedText));

        // Upload QR screenshot
        function scanFromFile(input){
            if(input.files.length===0) return;
            const file=input.files[0];
            const html5QrCode=new Html5Qrcode("reader");
            html5QrCode.scanFile(file,true)
                .then(decodedText => fetchStudentInfo(decodedText))
                .catch(err => document.getElementById('result').innerHTML="❌ Error scanning file: "+err);
        }
    </script>
</body>
</html>
