package com.example.fitnesstracker;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddLiftDialog {
    public interface AddLiftCallback {
        void onLiftEntered(String liftName);
    }

    private Context context;
    private AddLiftCallback callback;

    public AddLiftDialog(Context context, AddLiftCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void show() {
        LinearLayout layout = new LinearLayout(context);
        layout.setPadding(50, 40, 50, 10);
        EditText et = new EditText(context);
        et.setHint("Enter lift name");
        layout.addView(et);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Lift");
        builder.setView(layout);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String liftName = et.getText().toString().trim();
            if (liftName.isEmpty()) {
                Toast.makeText(context, "Lift name cannot be empty.", Toast.LENGTH_SHORT).show();
            } else {
                callback.onLiftEntered(liftName);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
