package bootpay.co.kr.samplepayment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.BootpayAnalytics;
import kr.co.bootpay.enums.UX;
import kr.co.bootpay.listner.CancelListener;
import kr.co.bootpay.listner.CloseListener;
import kr.co.bootpay.listner.ConfirmListener;
import kr.co.bootpay.listner.DoneListener;
import kr.co.bootpay.listner.ErrorListener;
import kr.co.bootpay.listner.ReadyListener;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;
import kr.co.bootpay.model.BootExtra;
import kr.co.bootpay.model.StatItem;
import kr.co.bootpay.model.BootUser;

public class NativeActivity extends Activity {
    private int stuck = 1;
//5b9f51264457636ab9a07cdc
//    private String application_id = "5b9f51264457636ab9a07cdc";
//    private String application_id = "5b14c0ffb6d49c40cda92c4e";

    private String application_id = "5ab306a4396fa616d1ba3e69";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);

        // 초기설정 - 해당 프로젝트(안드로이드)의 application id 값을 설정합니다. 결제와 통계를 위해 꼭 필요합니다.
        BootpayAnalytics.init(this, application_id);
//        BootpayAnalytics.init(this, "5b14c0ffb6d49c40cda92c4e");

        Bootpay.useOnestoreApi(this);

//        BootpayAnalytics.init(this, "59a7e647396fa64fcad4a8c2");

        // 통계 - 유저 로그인 시점에 호출
        BootpayAnalytics.login(
                "testUser", // bootUser 고유 id 혹은 로그인 아이디
                "testUser@gmail.com", // bootUser email
                "username", // bootUser 이름
                0, //1: 남자, 0: 여자
                "861014", // bootUser 생년월일 앞자리
                "01012345678", // bootUser 휴대폰 번호
                "충청"); //  서울|인천|대구|대전|광주|부산|울산|경기|강원|충청북도|충북|충청남도|충남|전라북도|전북|전라남도|전남|경상북도|경북|경상남도|경남|제주|세종 중 택 1

        startTrace();
    }

    public void startTrace() {
//        통계 - 페이지 추적
        List<StatItem> items = new ArrayList<>();
        items.add(new StatItem("마우스", "https://image.mouse.com/1234", "ITEM_CODE_MOUSE", "", "", ""));
        items.add(new StatItem("키보드", "https://image.keyboard.com/12345", "ITEM_CODE_KEYBOARD", "패션", "여성상의", "블라우스"));
        BootpayAnalytics.start("ItemListActivity", "item_list", items);
    }

    public void onClick_request(View v) {
        BootUser bootUser = new BootUser().setPhone("010-1234-5678");
        BootExtra bootExtra = new BootExtra().setQuotas(new int[] {0,2,3});

        Bootpay.init(getFragmentManager())
                .setContext(this)
                .setApplicationId("5b14c0ffb6d49c40cda92c4e") // 해당 프로젝트(안드로이드)의 application id 값
                .setPG(PG.INICIS) // 결제할 PG 사
//                .setUserPhone("010-1234-5678") // 구매자 전화번호
//                .setUX(UX.PG_SUBSCRIPT)
                .setMethod(Method.BANK) // 결제수단
                //.isShowAgree(true)
                .setName("맥북프로임다") // 결제할 상품명
                .setOrderId("1234") // 결제 고유번호
                .setPrice(1000) // 결제할 금액

                .addItem("마우스", 1, "ITEM_CODE_MOUSE", 100) // 주문정보에 담길 상품정보, 통계를 위해 사용
                .addItem("키보드", 1, "ITEM_CODE_KEYBOARD", 200, "패션", "여성상의", "블라우스") // 주문정보에 담길 상품정보, 통계를 위해 사용

                .request();

//        결제호출
//        Bootpay.init(getFragmentManager())
//                .setApplicationId(application_id) // 해당 프로젝트(안드로이드)의 application id 값
//                .setPG(PG.DANAL) // 결제할 PG 사
//                .setContext(this)
//                .setBootUser(bootUser)
////                .setD
//                .setBootExtra(bootExtra)
////                .setUserPhone("010-1234-5678") // 구매자 전화번호
//                .setMethod(Method.CARD) // 결제수단
////                .setUX(UX.PG_SUBSCRIPT)
//                .setName("맥\"북프로's 임다") // 결제할 상품명
//                .setOrderId("1234") // 결제 고유번호expire_month
////                .setAccountExpireAt("2018-09-22") // 가상계좌 입금기간 제한 ( yyyy-mm-dd 포멧으로 입력해주세요. 가상계좌만 적용됩니다. 오늘 날짜보다 더 뒤(미래)여야 합니다 )
////                .setQuotas(new int[] {0,2,3}) // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)
//                .setPrice(1000) // 결제할 금액
//                .addItem("마우's 스", 1, "ITEM_CODE_MOUSE", 3000) // 주문정보에 담길 상품정보, 통계를 위해 사용
//                .addItem("키보드", 1, "ITEM_CODE_KEYBOARD", 7000, "패션", "여성상의", "블라우스") // 주문정보에 담길 상품정보, 통계를 위해 사용
//                .onConfirm(new ConfirmListener() { // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
//                    @Override
//                    public void onConfirm(@Nullable String message) {
//
//                        if (0 < stuck) Bootpay.confirm(message); // 재고가 있을 경우.
//                        else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
//                        Log.d("confirm", message);
//                    }
//                })
//                .onDone(new DoneListener() { // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
//                    @Override
//                    public void onDone(@Nullable String message) {
//                        Log.d("done", message);
//                    }
//                })
//                .onReady(new ReadyListener() { // 가상계좌 입금 계좌번호가 발급되면 호출되는 함수입니다.
//                    @Override
//                    public void onReady(@Nullable String message) {
//                        Log.d("ready", message);
//                    }
//                })
//                .onCancel(new CancelListener() { // 결제 취소시 호출
//                    @Override
//                    public void onCancel(@Nullable String message) {
//
//                        Log.d("cancel", message);
//                    }
//                })
//                .onError(new ErrorListener() { // 에러가 났을때 호출되는 부분
//                    @Override
//                    public void onError(@Nullable String message) {
//                        Log.d("error", message);
//                    }
//                })
//                .onClose(
//                        new CloseListener() { //결제창이 닫힐때 실행되는 부분
//                    @Override
//                    public void onClose(String message) {
//                        Log.d("close", "close");
//                    }
//                })
//                .request();
    }
}
