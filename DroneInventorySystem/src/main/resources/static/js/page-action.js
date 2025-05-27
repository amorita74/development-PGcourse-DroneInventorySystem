/**
 * 前のページに戻る
 */
function goBack() {
  window.history.back();
}

/**
 * 削除フラグをonにする
 */

function submitWithFlag(isDelete) {
  // 削除フラグの値を設定
  document.getElementById('deleteFlag').value = isDelete;
  // フォームを送信
  document.getElementById('updateForm').submit();
}
