package co.tinode.tindroid;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;

public class QRCodeFragment extends BaseFragment {

    String topicId;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        topicId = getArguments().getString("topicId");
        ImageView codeIv = view.findViewById(R.id.codeIv);
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            JSONObject info = new JSONObject();
            info.put("im_id", topicId);
            Bitmap bitmap = barcodeEncoder.encodeBitmap(info.toString(), BarcodeFormat.QR_CODE, 400, 400);
            codeIv.setImageBitmap(bitmap);
        } catch (Exception e) {

        }
        view.findViewById(R.id.save_button).setOnClickListener(v -> {
            ImageView codeIv1 = view.findViewById(R.id.codeIv);
            Bitmap bmp = ((BitmapDrawable) codeIv1.getDrawable()).getBitmap();
            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bmp, "qrcode", null);
            Toast.makeText(getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();
        });
        TextView qrCodeTipTv = view.findViewById(R.id.qrcode_tip_tv);
        if (!TextUtils.isEmpty(topicId) && topicId.startsWith("usr")) {
            qrCodeTipTv.setText(R.string.qr_code_tip);
        } else {
            qrCodeTipTv.setText(R.string.qr_code_group_tip);
        }
    }

    @Override
    int getLayout() {
        return R.layout.fragment_qr_code;
    }
}
