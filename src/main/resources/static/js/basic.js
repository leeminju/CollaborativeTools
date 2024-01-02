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
        type: 'GET', url: `/api/users`, contentType: 'application/json', success: function (response) {
            if (!response['data']) {
                CookieRemove();
            }
            userId = Number(response['data']['id']);
            $('#username').text(response['data']['username']);
        }, error(error, status, request) {
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
        type: 'DELETE', url: `/api/users/logout`, success: function (response) {
            alert(response['msg']);
            CookieRemove();
        }, error(error, status, request) {
            console.log(error);
        }
    });

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
        'currentPassword': currentPassword, 'newPassword': newPassword, 'confirmPassword': confirmPassword
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
    });
}

//회원탈퇴
function unRegister() {

    $.ajax({
        type: 'DELETE', url: `/api/users/${userId}`, success: function (response) {
            alert(response['msg']);
            logout();
        }, error(error, status, request) {
            alert(error['responseJSON']['msg']);
        }
    });
}

function getBoardList() {
    $.ajax({
        type: 'GET', url: `/api/boards`, success: function (response) {
            for (var i = 0; i < response['data'].length; i++) {
                let board = response['data'][i];
                console.log(board);

                let boardId = board['boardId'];
                let title = board['title'];

                let desc1 = board['desc'];
                let desc2 = board['desc'].replace(/\n/g, "\\n");
                let backgroundColor = board['backgroundColor'];


                let tempHtml = `<li class="list-group-item" style="border: black solid 1px;background-color: ${backgroundColor}"
                        onclick = "showBoardDetails('${boardId}','${title}','${desc2}','${backgroundColor}')">${title}
                    <button onclick="updateBoardModal('${boardId}','${title}','${desc2}','${backgroundColor}')" data-bs-toggle="modal" data-bs-target="#updateBoard" style="float: right" class="btn btn-outline-dark">edit</button>
                    <button onclick="removeBoard(${boardId})" style="float: right" class="btn btn-outline-dark">remove</button></li>`;

                $('#board_list').append(tempHtml);
                if (i == 0) {
                    showBoardDetails(boardId, title, desc1, backgroundColor);
                }

            }

            let button = `<button data-bs-toggle="modal"
                                                data-bs-target="#createBoard">게시판 추가</button>`
            $('#board_list').append(button);
        }, error(error, status, request) {
            alert(error['responseJSON']['msg']);
        }
    });
}

function removeBoard(boardId) {
    $.ajax({
        type: 'DELETE', url: `/api/boards/${boardId}`, success: function (response) {
            alert(response['msg']);
            win_reload();
        }, error(error, status, request) {
            alert(error['msg']);
        }
    });
}

// 보드 상세 조회
function showBoardDetails(boardId, title, desc, backgroundColor) {

    $('#board_title').text(title);
    $('#board_desc').text(desc);
    $('#board_background').css("background-color", backgroundColor);
    $('#invite_btn').val(boardId);
    //상세 조회 - 컬럼 붙이기

    $.ajax({
        type: 'GET', url: `/api/boards/${boardId}`, success: function (response) {
            let columns = response['data'];
            $('#column_list').empty();
            console.log(columns);
            for (var i = 0; i < columns.length; i++) {
                let column = columns[i];
                let columnId = column['columnId'];
                let title = column['columnTitle'];
                let cards = column['cardTitleList'];

                let html = `<div class="card text-dark bg-light mb-3" style="height:fit-content; max-width:18rem; width: 18rem; margin: 10px">
                                        <div class="card-header">${title}</div>
                                          <div class="card-body">
                                                <ul class="list-group list-group-flush" id="card_list-${columnId}">
                                                 </ul>
                                                <button id="add_card_btn-${columnId}" onclick="showCardTitleArea(${columnId})" style="width:100%;border: transparent;background-color: transparent">+ Add a card</button>
                                                     
                                                <input style="display: none; width:100%;" placeholder="카드 제목을 입력하세요" id="card_title_input-${columnId}">      
                                                <span>        
                                                <button onclick="addCard('${columnId}')" style="display: none" class="btn btn-primary" id="add_card_btn2-${columnId}">add card</button>
                                                <button style="display: none;background-color: transparent; border: transparent"  onclick="hideCardTitleArea(${columnId})" id="close_card_add_btn-${columnId}">X</button>
                                                </span>
                                            </div>
                                        </div>`
                $('#column_list').append(html);

                for (var j = 0; j < cards.length; j++) {
                    let card = cards[j];
                    let cardId = card['cardId'];
                    let cardTitle = card['cardTitle'];
                    let backgroundColor = card['backgroundColor'];

                    let html = `<li class="list-group-item" style="background-color: ${backgroundColor};border: grey solid 1px; border-radius: 10px;margin-bottom: 8px">${cardTitle}</li>`;
                    $("#card_list-" + columnId).append(html);
                }


            }
            let html = `<div class="card text-dark bg-light mb-3" style="height:fit-content; max-width:18rem; width: 18rem; margin: 10px">                                    
                                          <div class="card-body">
                                               <button id="add_list_btn-${boardId}"onclick="showColumnTitleArea('${boardId}')" style="width:100%;border: transparent;background-color: transparent">+ Add another list</button>
                                               <input  id="column_title_input-${boardId}"style="display: none" placeholder="리스트 제목을 입력하세요" style="display:none;margin: 100%">      
                                               <span>
                                               <button id="add_list_btn2-${boardId}" style="display: none" class="btn btn-primary">Add list</button>
                                               <button id="close_add_list_btn-${boardId}" style="background-color: transparent;display: none; border: transparent"  onclick="hideColumnTitleArea(${boardId})">X</button>
                                               </span>
                                            </div>
                                        </div>`
            $('#column_list').append(html);

        }, error(error, status, request) {
            alert(error['msg']);
        }
    });
}

//리스트 추가 시 제목 입력 보이게
function showColumnTitleArea(boardId) {
    $('#add_list_btn-' + boardId).hide();

    $('#column_title_input-' + boardId).show();
    $('#add-list_btn2-' + boardId).show();
    $('#close_add_list_btn-' + boardId).show();
}

function hideColumnTitleArea(boardId) {
    $('#add_list_btn-' + boardId).show();

    $('#column_title_input-' + boardId).hide();
    $('#add-list_btn2-' + boardId).hide();
    $('#close_add_list_btn-' + boardId).hide();
}

// 카드 추가 버튼 클릭시 제목 입력 보이게
function showCardTitleArea(columnId) {
    $("#card_title_input-" + columnId).show();
    $('#add_card_btn2-' + columnId).show();
    $('#close_card_add_btn-' + columnId).show();

    $('#add_card_btn-' + columnId).hide();
}

function hideCardTitleArea(columnId) {
    $("#card_title_input-" + columnId).hide();
    $('#add_card_btn2-' + columnId).hide();
    $('#close_card_add_btn-' + columnId).hide();

    $('#add_card_btn-' + columnId).show();
}

//보드 생성
function createBoard() {
    let title = $('#create_board_title').val();
    let desc = $('#create_board_desc').val();
    let backgroundColor = $('#create_background_color').val();

    let data = {
        'title': title, 'desc': desc, 'backgroundColor': backgroundColor
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
    });
}

//보드 변경 모달에 값 넣기
function updateBoardModal(boardId, title, desc, backgroundColor) {
    $('#board_update_btn').val(boardId);
    $('#update_board_title').val(title);
    $('#update_board_desc').val(desc);
    $('#update_background_color').val(backgroundColor);
}

//보드 수정
function updateBoard() {
    let boardId = $('#board_update_btn').val();
    let title = $('#update_board_title').val();
    let desc = $('#update_board_desc').val();
    let backgroundColor = $('#update_background_color').val();

    let data = {
        'title': title, 'desc': desc, 'backgroundColor': backgroundColor
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
    });
}

//보드 멤버 조회
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
                let role = member['role'];
                let temp = `<li class="list-group-item" style="border: black solid 1px" >${username}(${role})</li>`;
                $('#board_member_list').append(temp);
            }
        },
        error(error, status, request) {

        }
    });
}

//보드 멤버 초대
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

function addCard(columnId) {
    let title = $('#card_title_input-' + columnId).val();

    let data = {
        'title': title
    };

    $.ajax({
        type: 'POST',
        url: `/api/${columnId}/cards`,
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

                if (valid['title']) {
                    str += valid['title'] + "\n";
                }
                alert(str);
            } else {
                alert(error['responseJSON']['msg']);
            }
        }
    });
}