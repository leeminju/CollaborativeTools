const host = 'http://' + window.location.host;
var userId = -1;
let current_boardId = 0;
let current_cardId = -1;
let current_cardInfo = null;
let prev_columnId = -1;
let prev_sequence = -1;
let edit_card_title = false;
// 화면 시작하자마자
$(document).ready(function () {
    $("#update_card_background_color").on("change", function (event) {
        let updateColor = $("#update_card_background_color").val();
        $('#card-content').css("background-color", updateColor);
        updateBackgroundColor(updateColor);
    });
    authorizationCheck();//인가
    getBoardList();
})

$('html').click(function (e) {
    let targetId = e.target.id;
    if (prev_columnId !== -1) {
        if (targetId !== `colum_title-${prev_columnId}` && targetId !== `column_title_input-${prev_columnId}`) {
            updateColumn(prev_columnId);
        }
    }

    if (edit_card_title) {
        if (targetId !== 'card_title' && targetId !== 'card_title_input') {
            updateCardTitle();
        }
    }
});

function updateColumn(prev_columnId) {
    let title = $(`#column_title_input-${prev_columnId}`).val();
    let data = {
        'boardId': current_boardId,
        'title': title,
        'sequence': prev_sequence

    }
    $.ajax({
        type: 'PUT',
        url: `/api/columns/${prev_columnId}`,
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (response) {
            $('#colum_title-' + prev_columnId).text(title);
            $('#colum_title-' + prev_columnId).show();
            $('#colum_title-' + prev_columnId).show();
            $('#column_title_input-' + prev_columnId).hide();
            prev_columnId = -1;
        },
        error(error, status, request) {
            console.log(error);
        }
    });
}

//드래깅 대상 생성
const dragger = {
    init(target, options) {
        return dragula(
            [
                ...target // 해당 리스트의 아이템을 드래그 가능하게 만듦
            ],
            options
        );
    },
    setSiblings({el, target, items, type}) {
        // 계산에 사용할 앞과 뒤의 카드 선언
        let prev = null;
        let next = null;

        console.log("itemlist");
        console.log(items)
        items.forEach((item, index, arr) => {
            console.log("index ->" + index);
            console.log(item)
            // 리스트의 아이템들을 순환
            if (item.dataset[type + "Id"] * 1 === el.dataset[type + "Id"] * 1) {
                console.log("cardId" + item.dataset[type + "Id"])
                // 현재 드래그하는 카드일 경우
                prev = index > 0 ? arr[index - 1] : null; // 맨 위가 아닐 경우 이전 아이템 할당
                next = index < arr.length - 1 ? arr[index + 1] : null; // 맨 아래가 아닐 경우 다음 아이템 할당
            }
        });

        return {prev, next};
    }
};

function setCardDraggable() {
    if (this.dragulaCard) this.dragulaCard.destroy();

    const options = {};
    let cardList = Array.from(document.querySelectorAll(".list-group"));
    console.log(cardList)
    this.dragulaCard = dragger.init(
        Array.from(document.querySelectorAll(".list-group")),
        options
    );

    this.dragulaCard.on("drop", (el, target, source, sibling) => {
        // 이벤트 리스너 등록(el은 드래그하는 요소, target은 위에서 등록한 리스트)
        console.log("el")
        console.log(sibling)
        const targetCard = {
            id: el.dataset.cardId * 1,
            columnId: target.parentNode.dataset.columnId * 1,
            sequence: el.dataset.sequence * 1
        }; // api업데이트시 쓰일 객체 생성

        const {prev, next} = dragger.setSiblings({
            el,
            target,
            items: target.querySelectorAll(".list-group-item"),
            type: "card"
        });

        // 자리가 맨 위일 경우
        console.log("prev");
        console.log(prev);
        console.log("next");
        console.log(next)
        if (!prev && next) targetCard.sequence = 1;
        // 자리가 사이일 경우
        else if (prev && next) {
            let temp = targetCard.sequence;
            console.log("silbing")
            console.log(sibling)
            targetCard.sequence = (next.dataset.sequence * 1);
            //sibling.sequence = temp;
        }
        // 자리가 맨 아래일 경우
        else if (prev && !next) targetCard.sequence = (prev.dataset.sequence * 1) + 1;

        this.updateCardsequence(targetCard);
    });
}

function updateCardsequence(card) {
    console.log("update card")
    $.ajax({
        type: 'PUT',
        url: `/api/boards/${current_boardId}/columns/${card.columnId}/cards/${card.id}/sequence`,
        contentType: 'application/json',
        data: JSON.stringify(card),
        success: function (response) {
            console.log(response);
            alert(response['msg']);
            win_reload();
        },
        error(error, status, request) {
            alert(card)
        }
    });
}


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

                let boardId = board['boardId'];
                let title = board['title'];

                let desc1 = board['desc'];
                let desc2 = board['desc'].replace(/\n/g, "\\n");
                let backgroundColor = board['backgroundColor'];


                let tempHtml = `<li class="list-group-item" style="border: black solid 1px;background-color: ${backgroundColor}"
                        onclick = "showBoardDetails('${boardId}','${title}','${desc2}','${backgroundColor}')">${title}
                    <div style="float:right;display: inline-block; ">     
                    <button onclick="updateBoardModal('${boardId}','${title}','${desc2}','${backgroundColor}')" data-bs-toggle="modal" data-bs-target="#updateBoard" class="btn btn-outline-dark">edit</button>
                    <button onclick="removeBoard(${boardId})" class="btn btn-outline-dark">remove</button></div></li>`;

                $('#board_list').append(tempHtml);
                if (i == 0) {
                    current_boardId = boardId;
                    console.log(current_boardId);
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
            alert(error['responseJSON']['msg']);
        }
    });
}

function showEditTitle(columnId, sequence) {
    if (prev_columnId !== -1 && prev_columnId !== columnId) {
        // $('#colum_title-' + prev_columnId).show();
        // $('#column_title_input-' + prev_columnId).hide();
        updateColumn(prev_columnId);
    }

    $('#colum_title-' + columnId).hide();
    $('#column_title_input-' + columnId).show();
    prev_columnId = columnId;
    prev_sequence = sequence;
}


// 보드 상세 조회
function showBoardDetails(boardId, title, desc, backgroundColor) {
    current_boardId = boardId;
    prev_columnId = -1;
    $('#board_title').text(title);
    $('#board_desc').text(desc);
    $('#board_background').css("background-color", backgroundColor);
    $('#invite_btn').val(boardId);
    //상세 조회 - 컬럼 붙이기

    $.ajax({
        type: 'GET', url: `/api/boards/${boardId}`, success: function (response) {
            let columns = response['data'];
            let last_sequence = 0;
            $('#column_list').empty();

            for (var i = 0; i < columns.length; i++) {
                let column = columns[i];
                let columnId = column['columnId'];
                let title = column['columnTitle'];
                let sequence = column['sequence'];
                let cards = column['cardTitleList'];
                last_sequence = column['sequence'];

                let html = `<div class="card text-dark bg-light mb-3" style="height:fit-content; max-width:18rem; width: 18rem; margin: 10px" xmlns="http://www.w3.org/1999/html">
                                        <div class="card-header">
                                        <span onclick="showEditTitle('${columnId}','${sequence}')" id="colum_title-${columnId}">${title}</span>
                                        <input class = "column_title_input" style="display: none;width: 75%" value="${title}" id="column_title_input-${columnId}">
                                         <div class="btn-group" style="float: right">
  <button type="button" class="btn dropdown"  style="background-color: transparent;border:transparent;" data-bs-toggle="dropdown" aria-expanded="false">
    • • • 
  </button>
  <ul class="dropdown-menu">
    <li><a class="dropdown-item" onclick="removeColumn('${columnId}')">Remove</a></li>
    </ul>
                                            </div>
                                         </div>
                                          <div class="card-body" data-column-id="${columnId}" data-sequence="${sequence}">
                                                <ul class="list-group list-group-flush" id="card_list-${columnId}">
                                                <br></br>
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
                    let sequence = card['sequence'];
                    let html = `<li class="list-group-item" onclick="showCardDetails('${cardId}','${columnId}')"  data-bs-toggle="modal" data-bs-target="#CardModal"
                    style="background-color: ${backgroundColor};border: grey solid 1px; border-radius: 10px;margin-bottom: 8px" data-card-id="${cardId}" data-sequence=${sequence}>
                            ${cardTitle}
                        </li>`;
                    console.log($("#card_list-" + columnId).children('br').remove())

                    $("#card_list-" + columnId).append(html);
                }


            }
            let html = `<div class="card text-dark bg-light mb-3" style="height:fit-content; max-width:18rem; width: 18rem; margin: 10px">                                    
                                          <div class="card-body">
                                               <button id="add_list_btn-${boardId}"onclick="showColumnTitleArea('${boardId}')" style="width:100%;border: transparent;background-color: transparent">+ Add another list</button>
                                               <input  id="new_column_title_input-${boardId}" placeholder="리스트 제목을 입력하세요"
                                                style="display:none;width: 100%"
                                                >      
                                               <span>
                                               <button onclick="addColumn('${boardId}','${last_sequence}')" id="add_list_btn2-${boardId}" style="display: none" class="btn btn-primary">Add list</button>
                                               <button id="close_add_list_btn-${boardId}" style="background-color: transparent;display: none; border: transparent"  onclick="hideColumnTitleArea(${boardId})">X</button>
                                               </span>
                                            </div>
                                        </div>`
            $('#column_list').append(html);

            setCardDraggable();
        }, error(error, status, request) {
            alert(error['msg']);
        }
    });
}

//리스트 추가 시 제목 입력 보이게
function showColumnTitleArea(boardId) {
    console.log("컬럼 제목 입력 보이기")
    $('#add_list_btn-' + boardId).hide();

    $('#new_column_title_input-' + boardId).show();
    $('#add_list_btn2-' + boardId).show();
    $('#close_add_list_btn-' + boardId).show();
}

function hideColumnTitleArea(boardId) {
    console.log("컬럼 제목 입력 숨기기")
    $('#add_list_btn-' + boardId).show();

    $('#new_column_title_input-' + boardId).hide();
    $('#add_list_btn2-' + boardId).hide();
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
        url: `/api/boards/${current_boardId}/columns/${columnId}/cards`,
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (response) {
            alert(response['msg']);
            win_reload();
        },
        error(error, status, request) {
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

function addColumn(boardId, last_sequence) {
    let title = $('#new_column_title_input-' + boardId).val();
    let sequence = last_sequence + 1;


    let data = {
        'boardId': boardId, 'title': title, 'sequence': sequence
    };

    $.ajax({
        type: 'POST',
        url: `/api/columns`,
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (response) {
            alert(response['msg']);
            win_reload();
        },
        error(error, status, request) {
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


function removeColumn(columnId) {
    $.ajax({
        type: 'DELETE', url: `/api/columns/${columnId}`,
        success: function (response) {
            alert(response['msg']);
            win_reload();
        }, error(error, status, request) {
            alert(error['responseJSON']['msg']);
        }
    });
}

function unRegisterInBoard() {

    $.ajax({
        type: 'DELETE', url: `/api/boards/${current_boardId}/users`,
        success: function (response) {
            alert(response['msg']);
            win_reload();
        }, error(error, status, request) {
            alert(error['responseJSON']['msg']);
        }
    });
}

//카드상세조회
function showCardDetails(cardId, columnId) {
    current_cardId = cardId;
    $.ajax({
        type: 'GET', url: `/api/boards/${current_boardId}/columns/${columnId}/cards/${cardId}`
        , success: function (response) {
            let card = response['data'];
            current_cardInfo = card;
            let desc = card['description'];
            let title = card['title'];
            let dueDate = card['dueDate'];
            let backgroundColor = card['backgroundColor'];

            if (dueDate != null) {
                $('#card_due_date_input').val(dueDate);
                $('#card_due_date').text(dueDate);
            }
            if (backgroundColor != null) {
                $('#card-content').css("background-color", backgroundColor);
                $('#update_card_background_color').val(backgroundColor);
            }

            $('#card_title').text(title);
            $('#card_desc').attr("readonly", true).val(desc).css("background-color", "darkgray");
            $('#desc_save_btn').hide();

        }, error(error, status, request) {
            alert(error['responseJSON']['msg']);
        }
    });

}

function enableTextArea() {
    console.log("클릭");
    $('#desc_save_btn').show();
    $('#card_desc').attr("readonly", false).css("background-color", "white");
}

function saveCardDescription() {
    let columnId = current_cardInfo['columnId'];
    let desc = $('#card_desc').val();
    let dueDate;
    if (current_cardInfo['dueDate'] == null) {
        dueDate = null
    } else {
        dueDate = current_cardInfo['dueDate'].substring(0, 16);
    }

    data = {
        'title': current_cardInfo['title'],
        'description': desc,
        'backgroundColor': current_cardInfo['backgroundColor'],
        'sequence': current_cardInfo['sequence'],
        'dueDate': dueDate
    }

    $.ajax({
        type: 'PUT',
        url: `/api/boards/${current_boardId}/columns/${columnId}/cards/${current_cardId}/update`,
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (response) {
            showCardDetails(current_cardId, columnId)
        },
        error(error, status, request) {
            console.log(error);
        }
    });

}

function showCardInput() {
    $('#card_title_input').show().val(current_cardInfo['title']);
    $('#card_title').hide();
    edit_card_title = true;
}

function updateCardTitle() {
    console.log(current_cardInfo);
    let columnId = current_cardInfo['columnId'];
    let title = $('#card_title_input').val()
    let dueDate;
    if (current_cardInfo['dueDate'] == null) {
        dueDate = null
    } else {
        dueDate = current_cardInfo['dueDate'].substring(0, 16);
    }


    data = {
        'title': title,
        'description': current_cardInfo['description'],
        'backgroundColor': current_cardInfo['backgroundColor'],
        'sequence': current_cardInfo['sequence'],
        'dueDate': dueDate
    }

    $.ajax({
        type: 'PUT',
        url: `/api/boards/${current_boardId}/columns/${columnId}/cards/${current_cardId}/update`,
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (response) {
            $('#card_title_input').hide().val(title);
            $('#card_title').show().text(title);
            edit_card_title = false;
            showCardDetails(current_cardId, columnId)
        },
        error(error, status, request) {
            console.log(error);
        }
    });
}

function saveDueDate() {
    let columnId = current_cardInfo['columnId'];
    let dueDate = $('#card_due_date_input').val()
    console.log(dueDate);

    data = {
        'title': current_cardInfo['title'],
        'description': current_cardInfo['description'],
        'backgroundColor': current_cardInfo['backgroundColor'],
        'sequence': current_cardInfo['sequence'],
        'dueDate': dueDate
    }

    $.ajax({
        type: 'PUT',
        url: `/api/boards/${current_boardId}/columns/${columnId}/cards/${current_cardId}/update`,
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (response) {
            $('#card_due_date_input').val(dueDate);
            $('#card_due_date').text(dueDate);
            showCardDetails(current_cardId, columnId)
        },
        error(error, status, request) {
            console.log(error);
        }
    });
}

function removeCard() {
    let columnId = current_cardInfo['columnId'];
    $.ajax({
        type: 'DELETE',
        url: `/api/boards/${current_boardId}/columns/${columnId}/cards/${current_cardId}`,
        success: function (response) {
            alert(response['msg'])
            showBoardDetails(current_boardId, columnId)
        },
        error(error, status, request) {
            alert(error['responseJSON']['msg'])
        }
    });
}

function updateBackgroundColor(updateColor) {
    let columnId = current_cardInfo['columnId'];
    let dueDate;
    if (current_cardInfo['dueDate'] == null) {
        dueDate = null
    } else {
        dueDate = current_cardInfo['dueDate'].substring(0, 16);
    }


    data = {
        'title': current_cardInfo['title'],
        'description': current_cardInfo['description'],
        'backgroundColor': updateColor,
        'sequence': current_cardInfo['sequence'],
        'dueDate': dueDate
    }

    $.ajax({
        type: 'PUT',
        url: `/api/boards/${current_boardId}/columns/${columnId}/cards/${current_cardId}/update`,
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (response) {
        },
        error(error, status, request) {
            console.log(error);
        }
    });
}