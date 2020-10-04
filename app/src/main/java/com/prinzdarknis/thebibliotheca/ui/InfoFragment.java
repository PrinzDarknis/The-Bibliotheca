package com.prinzdarknis.thebibliotheca.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.prinzdarknis.thebibliotheca.MainActivity;
import com.prinzdarknis.thebibliotheca.R;
import com.prinzdarknis.thebibliotheca.database.v1.DBAndroid;
import com.prinzdarknis.thebibliotheca.imageManager.IImageManager;
import com.prinzdarknis.thebibliotheca.managment.ProgramLogic;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        view.findViewById(R.id.deleteDBButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDB();
            }
        });

        view.findViewById(R.id.loadSampleButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSampleData();
            }
        });

        return view;
    }

    public void loadSampleData() {
        showDialog(R.string.appInfoloadSample_title, R.string.appInfoloadSample_details, new Callback() {
            @Override
            void callback() {
                //check Permission
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(),R.string.appInfoloadSample_no_inet_perm, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getContext(),R.string.appInfoloadSample_image_background, Toast.LENGTH_SHORT).show();
                ProgramLogic.getInstance().loadSampleData(new IImageManager.Callback() {
                    @Override
                    public void callback(final boolean success) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (success)
                                    Toast.makeText(getContext(),R.string.appInfoloadSample_image_success, Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getContext(),R.string.appInfoloadSample_image_fail, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    public void deleteDB() {
        showDialog(R.string.appInfoDeleteDB_title_1, R.string.appInfoDeleteDB_details_1, new Callback() {
            @Override
            void callback() {
                showDialog(R.string.appInfoDeleteDB_title_2, R.string.appInfoDeleteDB_details_2, new Callback() {
                    @Override
                    void callback() {
                        showDialog(R.string.appInfoDeleteDB_title_3, R.string.appInfoDeleteDB_details_3, new Callback() {
                            @Override
                            void callback() {
                                ProgramLogic.getInstance().deleteDatabase();
                                DBAndroid.initialize(getContext());
                                ProgramLogic.getInstance().newDatabase(DBAndroid.getInstance());
                                MainActivity.setBibNameInNav(getString(R.string.menu_noLibrary));
                            }
                        });
                    }
                });
            }
        });
    }

    private void showDialog(int title, int details, final Callback callback) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(title);
        TextView view = new TextView(getContext());
        view.setText(details);
        dialog.setView(view);

        dialog.setPositiveButton(R.string.dialog_positiv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.callback();
            }
        });

        dialog.setNegativeButton(R.string.dialog_negativ, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private abstract class Callback {
        abstract void callback();
    }
}
