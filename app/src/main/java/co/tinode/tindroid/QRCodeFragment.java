package co.tinode.tindroid;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;

public class QRCodeFragment extends BaseFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView codeIv = view.findViewById(R.id.codeIv);
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            JSONObject info = new JSONObject();
            info.put("id", Cache.getTinode().getMyId());
            Bitmap bitmap = barcodeEncoder.encodeBitmap(info.toString(), BarcodeFormat.QR_CODE, 400, 400);
            codeIv.setImageBitmap(bitmap);
        } catch (Exception e) {

        }
        view.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo
            }
        });
    }

    @Override
    int getLayout() {
        return R.layout.fragment_qr_code;
    }
}
