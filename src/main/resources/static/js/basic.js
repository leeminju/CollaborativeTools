const host = 'http://' + window.location.host;

// 화면 시작하자마자
$(document).ready(function () {
    authorizationCheck();//인가
})

// 인가 : 토큰 유효성 판단
function authorizationCheck() {
    const auth = getToken();

    if (auth !== undefined && auth !== '') {
        $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
            jqXHR.setRequestHeader('Authorization', auth);
        });
    } else {
        window.location.href = host + '/login';
        return;
    }

    //로그인한 회원 정보
    $.ajax({
        type: 'GET',
        url: `/api/user-info`,
        contentType: 'application/json',
    })
        .done(function (res, status, xhr) {
            const username = res;

            if (!username) {
                window.location.href = '/login';
                return;
            }

            $('#username').text(username);
        })
        .fail(function (jqXHR, textStatus) {
            logout();
        });
}

function logout() {
    // 토큰 삭제
    Cookies.remove('Authorization', {path: '/'});
    window.location.href = host + '/login';
}

function getToken() {
    let auth = Cookies.get('Authorization');

    if (auth === undefined) {
        return '';
    }

    return auth;
}

//modal 내 close 버튼 클릭 시 새로고침
function win_reload() {
    window.location.reload();
}

//비밀번호 변경
function updatePassword() {
    let currentPassword = $('#currentPassword').val();
    let newPassword = $('#newPassword').val();
    let confirmPassword = $('#confirmPassword').val();

    let data = {
        'currentPassword': currentPassword,
        'newPassword': newPassword,
        'confirmPassword': confirmPassword
    };

    $.ajax({
            type: 'PUT',
            url: '/api/users/password',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (response) {
                console.log(response);
                alert(response['msg']);
                win_reload();
            },
            error(error, status, request) {
                if (error['responseJSON']['data'] != null) {
                    let valid = error['responseJSON']['data'];
                    let str = "";

                    if (valid['newPassword']) {
                        str += valid['newPassword'] + "\n";
                    }

                    alert(str);
                } else {
                    alert(error['responseJSON']['msg']);
                }
            }
        }
    );
}