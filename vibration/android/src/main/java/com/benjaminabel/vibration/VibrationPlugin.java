package com.benjaminabel.vibration;

import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.os.VibratorManager;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

public class VibrationPlugin implements FlutterPlugin, MethodCallHandler {
    private MethodChannel methodChannel;
    private Vibrator vibrator;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        Context context = flutterPluginBinding.getApplicationContext();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "vibration");
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "vibrate":
                long duration = (long) call.arguments;
                vibrate(duration);
                result.success(null);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void vibrate(long duration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(duration);
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        methodChannel.setMethodCallHandler(null);
        methodChannel = null;
    }

    // For older Flutter projects using the old v1 Android embedding method
    @SuppressWarnings("deprecation")
    public static void registerWith(Registrar registrar) {
        VibrationPlugin plugin = new VibrationPlugin();
        plugin.vibrator = (Vibrator) registrar.context().getSystemService(Context.VIBRATOR_SERVICE);

        MethodChannel channel = new MethodChannel(registrar.messenger(), "vibration");
        channel.setMethodCallHandler(plugin);
    }
}
