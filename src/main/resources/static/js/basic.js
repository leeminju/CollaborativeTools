const host = 'http://' + window.location.host;
var userId = -1;
// 화면 시작하자마자
$(document).ready(function () {
    authorizationCheck();//인가
    getBoardList();
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
        url: `/api/users`,
        contentType: 'application/json',
        success: function (response) {
            if (!response['data']) {
                CookieRemove();
            }
            userId = Number(response['data']['id']);
            $('#username').text(response['data']['username']);
        },
        error(error, status, request) {
            console.log(error);
            CookieRemove();
        }
    });
}

function CookieRemove() {
    Cookies.remove('Authorization', {path: '/'});
    Cookies.remove('RefreshToken', {path: '/'});
    window.location.href = host + '/login';
}

function logout() {
    // 토큰 삭제

    $.ajax({
            type: 'DELETE',
            url: `/api/users/logout`,
            success: function (response) {
                alert(response['msg']);
                CookieRemove();
            },
            error(error, status, request) {
                console.log(error);
            }
        }
    );

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
            url: `/api/users/${userId}/password`,
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

//회원탈퇴
function unRegister() {

    $.ajax({
            type: 'DELETE',
            url: `/api/users/${userId}`,
            success: function (response) {
                alert(response['msg']);
                logout();
            },
            error(error, status, request) {
                alert(error['responseJSON']['msg']);
            }
        }
    );
}

function getBoardList() {
    $.ajax({
            type: 'GET',
            url: `/api/boards`,
            success: function (response) {
                for (var i = 0; i < response['data'].length; i++) {
                    let board = response['data'][i];
                    console.log(board);

                    let boardId = board['boardId'];
                    let title = board['title'];
                    let desc = board['desc'];
                    let backgroundColor = board['backgroundColor'];


                    let tempHtml = `<li class="list-group-item" style="border: black solid 1px;background-color: ${backgroundColor}"
                        onclick = "showBoardDetails('${boardId}','${title}','${desc}','${backgroundColor}')">${title}
                    <button onclick="updateBoardId('${boardId}','${title}','${desc}','${backgroundColor}')" data-bs-toggle="modal" data-bs-target="#updateBoard" style="float: right" class="btn btn-outline-dark">edit</button>
                    <button onclick="removeBoard(${boardId})" style="float: right" class="btn btn-outline-dark">remove</button></li>`;

                    $('#board_list').append(tempHtml);
                    if (i == 0) {
                        showBoardDetails(boardId, title, desc, backgroundColor);
                    }

                }

                let button = `<button data-bs-toggle="modal"
                                                data-bs-target="#createBoard">게시판 추가</button>`
                $('#board_list').append(button);
            },
            error(error, status, request) {
                alert(error['responseJSON']['msg']);
            }
        }
    );
}

function removeBoard(boardId) {
    $.ajax({
            type: 'DELETE',
            url: `/api/boards/${boardId}`,
            success: function (response) {
                alert(response['msg']);
                win_reload();
            },
            error(error, status, request) {
                alert(error['msg']);
            }
        }
    );
}

function showBoardDetails(boardId, title, desc, backgroundColor) {
    $('#board_title').text(title);
    $('#board_desc').text(desc);
    $('#board_background').css("background-color", backgroundColor);
    $('#invite_btn').val(boardId);
    //상세 조회 - 컬럼 붙이기
}

function createBoard() {
    let title = $('#create_board_title').val();
    let desc = $('#create_board_desc').val();
    let backgroundColor = $('#create_background_color').val();

    let data =
        {
            'title': title,
            'desc': desc,
            'backgroundColor': backgroundColor
        };

    $.ajax({
            type: 'POST',
            url: '/api/boards',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (response) {
                alert(response['msg']);
                win_reload();
            },
            error(error, status, request) {
                console.log(error);

                if (error['responseJSON']['data'] != null) {
                    let valid = error['responseJSON']['data'];
                    let str = "";

                    if (valid['desc']) {
                        str += valid['desc'] + "\n";
                    }

                    if (valid['title']) {
                        str += valid['title'] + "\n";
                    }
                    alert(str);
                } else {
                    alert(error['responseJSON']['msg']);
                }
            }
        }
    );
}

function updateBoardId(boardId, title, desc, backgroundColor) {
    $('#board_update_btn').val(boardId);
    $('#update_board_title').val(title);
    $('#update_board_desc').val(desc);
    $('#update_background_color').val(backgroundColor);
}

function updateBoard() {
    let boardId = $('#board_update_btn').val();
    let title = $('#update_board_title').val();
    let desc = $('#update_board_desc').val();
    let backgroundColor = $('#update_background_color').val();

    let data =
        {
            'title': title,
            'desc': desc,
            'backgroundColor': backgroundColor
        };

    $.ajax({
            type: 'PUT',
            url: `/api/boards/${boardId}`,
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (response) {
                alert(response['msg']);
                win_reload();
            },
            error(error, status, request) {
                console.log(error);

                if (error['responseJSON']['data'] != null) {
                    let valid = error['responseJSON']['data'];
                    let str = "";

                    if (valid['desc']) {
                        str += valid['desc'] + "\n";
                    }

                    if (valid['title']) {
                        str += valid['title'] + "\n";
                    }
                    alert(str);
                } else {
                    alert(error['responseJSON']['msg']);
                }
            }
        }
    );
}

function getBoardMember() {
    let boardId = $('#invite_btn').val();

    $.ajax({
        type: 'GET',
        url: `/api/boards/${boardId}/users`,
        contentType: 'application/json',
        success: function (response) {
            let members = response['data'];
            $('#board_member_list').empty();
            for (var i = 0; i < members.length; i++) {
                let member = members[i];
                let username = member['userName'];
                let temp = `<li class="list-group-item" style="border: black solid 1px" >${username}
                    <button onclick="" style="float: right" class="btn btn-outline-dark">삭제</button></li>`;
                $('#board_member_list').append(temp);
            }
        },
        error(error, status, request) {

        }
    });
}

function inviteMember() {
    let boardId = $('#invite_btn').val();
    let username = $('#invite_member_name').val();

    let data = {
        'username': username
    }

    $.ajax({
        type: 'POST',
        url: `/api/boards/${boardId}/users`,
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (response) {
            alert(response["msg"]);
            getBoardMember();
        },
        error(error, status, request) {
            alert(error['responseJSON']["msg"]);
        }
    });
}