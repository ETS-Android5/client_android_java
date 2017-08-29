package kr.co.bootpay;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;

import kr.co.bootpay.model.Item;
import kr.co.bootpay.model.Request;


public final class PaymentDialog extends DialogFragment {

    private BootpayWebView bootpay;
    private EventListener listener;
    private static Request result;

    /**
     * @see Builder
     * @deprecated
     */
    @Deprecated
    public PaymentDialog() {
        // not allowed
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        bootpay = new BootpayWebView(inflater.getContext());
        afterViewInit();
        setCancelable(false);
        return bootpay;
    }

    private PaymentDialog setOnResponseListener(EventListener listener) {
        this.listener = listener;
        return this;
    }

    private void afterViewInit() {
        if (bootpay != null) bootpay.setData(result)
                .setDialog(getDialog())
                .setOnResponseListener(listener);
    }

    private PaymentDialog setData(Request request) {
        PaymentDialog.result = request;
        return this;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        bootpay.setData(result);
    }
//
//    @Override
//    public void onCancel(DialogInterface dialog) {
//        super.onCancel(dialog);
//        result = null;
//    }
//
//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        super.onDismiss(dialog);
//        result = null;
//    }

    private void transactionConfirm(String data) {
        bootpay.transactionConfirm(data);
    }

    public static final class Builder {
        private WeakReference<FragmentManager> fragmentManager;
        private Request result;
        private EventListener listener;
        private ErrorListener error;
        private DoneListener done;
        private CancelListener cancel;
        private ConfirmListener confirm;
        private PaymentDialog dialog;

        private Builder() {
            // not allowed
        }

        public Builder(@NonNull FragmentManager fm) {
            result = new Request();
            fragmentManager = new WeakReference<>(fm);
        }

        public Builder setApplicationId(@NonNull String id) {
            result.setApplication_id(id);
            return this;
        }

        public Builder setPrice(@IntRange(from = 0) int price) {
            result.setPrice(price);
            return this;
        }

        public Builder setPG(@NonNull String pg) {
            result.setPg(pg);
            return this;
        }

        public Builder setPG(@NonNull PG pg) {
            switch (pg) {
                case BOOTPAY:
                    result.setPg("bootpay");
                    break;
                case PAYAPP:
                    result.setPg("payapp");
                    break;
                case DANAL:
                    result.setPg("danal");
                    break;
                case KCP:
                    result.setPg("kcp");
                    break;
                case INICIS:
                    result.setPg("inicis");
                    break;
            }
            return this;
        }

        public Builder setName(@NonNull String name) {
            result.setName(name);
            return this;
        }

        public Builder addItem(@NonNull String name, @IntRange(from = 1) int quantity, @NonNull String primaryKey, @IntRange(from = 0) int price) {
            result.addItem(new Item(name, quantity, primaryKey, price));
            return this;
        }

        public Builder addItem(Item item) {
            result.addItem(item);
            return this;
        }

        public Builder addItems(Collection<Item> items) {
            if (items != null) for (Item i : items) addItem(i);
            return this;
        }

        public Builder setItems(@NonNull List<Item> items) {
            result.setItems(items);
            return this;
        }

        public Builder setOrderId(@NonNull String orderId) {
            result.setOrder_id(orderId);
            return this;
        }

        public Builder setMethod(@NonNull String method) {
            result.setMethod(method);
            return this;
        }

        public Builder setModel(@NonNull Request request) {
            result = request;
            return this;
        }

        public Builder setMethod(@NonNull Method method) {
            switch (method) {
                case CARD:
                    result.setMethod("card");
                    break;
                case CARD_SIMPLE:
                    result.setMethod("card_simple");
                    break;
                case BANK:
                    result.setMethod("bank");
                    break;
                case VBANK:
                    result.setMethod("vbank");
                    break;
                case PHONE:
                    result.setMethod("phone");
                    break;
                case SELECT:
                    result.setMethod("");
                    break;
            }
            return this;
        }

        public Builder setEventListener(@Nullable EventListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setParams(Object params) {
            result.setParams(params);
            return this;
        }

        public Builder setParams(String params) {
            result.setParams(params);
            return this;
        }

        public Builder setParams(JSONObject params) {
            result.setParams(params);
            return this;
        }

        public Builder onCancel(@NonNull CancelListener listener) {
            cancel = listener;
            return this;
        }

        public Builder onConfirm(@NonNull ConfirmListener listener) {
            confirm = listener;
            return this;
        }

        public Builder onDone(@NonNull DoneListener listener) {
            done = listener;
            return this;
        }

        public Builder onError(@NonNull ErrorListener listener) {
            error = listener;
            return this;
        }

        /**
         * Must have value:
         *
         * @see Request#application_id { @link https://alf001.bomgil.in/project/app }
         * @see Request#pg { @value "bootpay", "payapp", "danal", "kcp", "inicis" }
         * @see Request#price
         * @see Request#order_id
         */
        public void show() {

            if (isNullOrEmpty(result.getApplication_id()))
                error("Application id is not configured.");

            if (isNullOrEmpty(result.getPg()))
                error("PG is not configured.");

            if (isNullOrEmpty(result.getPrice()))
                error("Price is not configured.");

            if (isNullOrEmpty(result.getOrderId()))
                error("Order id is not configured.");

            if (listener == null && (error == null || cancel == null || confirm == null || done == null))
                error("Must to be required to handle events.");

            dialog = new PaymentDialog()
                    .setData(result)
                    .setOnResponseListener(listener == null ? new EventListener() {
                        @Override
                        public void onError(String message) {
                            if (error != null) error.onError(message);
                        }

                        @Override
                        public void onCancel(String message) {
                            if (cancel != null) cancel.onCancel(message);
                        }

                        @Override
                        public void onConfirmed(String message) {
                            if (confirm != null) confirm.onConfirmed(message);
                        }

                        @Override
                        public void onDone(String message) {
                            if (done != null) done.onDone(message);
                        }
                    } : listener);
            dialog.onCancel(new DialogInterface() {
                @Override
                public void cancel() {
                    dialog.bootpay.destroy();
                    dialog = null;
                    BootpayDialog.finish();

                }

                @Override
                public void dismiss() {
                    dialog.bootpay.destroy();
                    dialog = null;
                    BootpayDialog.finish();
                }
            });
            if (!fragmentManager.get().isDestroyed()) dialog.show(fragmentManager.get(), "dialog");
        }

        public void transactionConfirm(String data) {
            if (dialog != null) dialog.transactionConfirm(data);
        }

        private boolean error(String message) {
            throw new RuntimeException(message);
        }

        private boolean isNullOrEmpty(String value) {
            return (value == null) || (value.length() <= 0);
        }

        private boolean isNullOrEmpty(int value) {
            return value <= 0;
        }
    }

}