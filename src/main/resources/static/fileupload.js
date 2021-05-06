const baseUrl = "http://127.0.0.1:9001/api/";
var fileObj = [];




/**
 * byte 용량을 받아서 형 변환 후 리턴해주는 함수
 * 2016-06-20
 * @param bytes
 * @returns {String} 변환 값
 */
function gfnByteCalculation(bytes) {
    if(bytes < 0){
        return 0+" bytes";
    }
    var bytes = parseInt(bytes);
    var s = ['bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB'];
    var e = Math.floor(Math.log(bytes)/Math.log(1024));

    if(e == "-Infinity") return "0 "+s[0];
    else
        return (bytes/Math.pow(1024, Math.floor(e))).toFixed(2)+" "+s[e];
}

function gfnIsNull(sValue) {
    if (String(sValue).valueOf() == "undefined") {
        return true;
    }
    if (sValue == null) {
        return true;
    }
    if (("x" + sValue == "xNaN") && (new String(sValue.length).valueOf() == "undefined")) {
        return true;
    }
    if (sValue.length == 0) {
        return true;
    }
    if (sValue == "NaN") {
        return true;
    }
    return false;
}

function fnFileTextSpanVal(obj, fileOrd) {
    //허용 확장자
    var fileExtsnWhiteList = ["image/jpeg", "image/png", "image/gif"];

    //용량 제한
    var maxFileSize = 1048576;

    var fileName = obj.value;
    // 크롬의 경우 파일 선택 창에서 취소 하면 file 정보가 사라짐
    if (!gfnIsNull(fileName)) {
        //확장자 체크
        var fileExtsn = obj.files[0].type;
        if (fileExtsnWhiteList.indexOf(fileExtsn) == -1) {
            gfnConfirmFn("A", "허용되는 확장자가 아닙니다.");
            return false;
        }

        //용량 체크
        if (obj.files[0].size > maxFileSize) {
            gfnConfirmFn("A", "용량은 1MB를 초과 할 수 없습니다.");
            return false;
        }

        fileObj[fileOrd] = obj.files[0];
    } else {
        // 파일 정보가 사라졌을 경우 파일명 표시하는 부분 초기화
        obj.html("");
    }
}


function insert() {
    var formData = new FormData();

    formData.append("usrId", "123456789");
    formData.append("entId", "ENT2020070900001");
    $.each(fileObj, function (idx, map) {
        formData.append("file" + idx, map);
    });

    for (let key of formData.keys()) {
        console.log(key,":",formData.get(key));
    }
    $.ajax({
        url: baseUrl + "prod/insert",
        data: formData,
        dataType: "json",
        processData: false,
        contentType: false,
        cache: false,
        type: "POST",
        success: function (data, status, xhr) {
            data = JSON.parse(data);
            console.log(data);
        },
        error: function (xhr, status, err) {
            console.error(err);
        }
    });

}
