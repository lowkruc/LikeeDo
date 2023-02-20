package com.app.likee.video.downloader.nowatermark.fragments.galery;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.likee.video.downloader.nowatermark.R;
import com.app.likee.video.downloader.nowatermark.database.RealmViewModel;
import com.app.likee.video.downloader.nowatermark.fragments.player.VideoPlayerActivity;
import com.app.likee.video.downloader.nowatermark.models.DownloadModel;
import com.app.likee.video.downloader.nowatermark.system.UiUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GaleryFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayout item, nodata;
    private GaleryAdapter adapter;
    private SelectionTracker selectionTracker;
    private RealmViewModel realmViewModel;
    private LinearLayoutManager linearLayoutManager;
    private RelativeLayout toolbarDelete;
    private TextView totalFileDelete, dowmloadNow;
    private FrameLayout closeDeleteItem, btnDeleteFile;
    private UiUtils uiUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_galery, container, false);
        uiUtils = new UiUtils(getActivity());
        setupUI(view);
        setupReycircleView();
        setupViewModel();
        setupSelectionTracker();
        setupHendleButton();
        return view;
    }

    private void setupHendleButton() {
        btnDeleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDelete();
            }
        });
        closeDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionTracker.clearSelection();
            }
        });
        dowmloadNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiUtils.launchNewActivity(v.getContext(), "video.like");
            }
        });
    }

    void showDialogDelete() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setView(LayoutInflater.from(getContext()).inflate(R.layout.widget_remove_dialog, null));
        AlertDialog alertDialog = dialog.create();
        Window window = alertDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        TextView delete = (TextView) alertDialog.findViewById(R.id.deleteApprove);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFileAction();
                alertDialog.dismiss();
            }
        });
    }

    private void deleteFileAction() {
        if (selectionTracker.getSelection().size() > 0) {
            List<Long> id = new ArrayList<>();
            for (Object d : selectionTracker.getSelection()) {
                id.add((long) d);
            }
            realmViewModel.bulkRemoData(id);
            selectionTracker.clearSelection();
        }
        if (adapter.getItemCount() == 0) {
            item.setVisibility(View.GONE);
            nodata.setVisibility(View.VISIBLE);
        }
    }


    private void setupSelectionTracker() {
        selectionTracker = new SelectionTracker.Builder<Long>(
                "Id",
                recyclerView,
                new GaleryAdapter.ItemKeyProvider(adapter),
                new GaleryAdapter.MyItemLookup(recyclerView),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build();
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                adapter.setCheckLayout(selectionTracker.hasSelection());
                if (selectionTracker.hasSelection()) {
                    totalFileDelete.setText("Delete " + selectionTracker.getSelection().size() + " files");
                    toolbarDelete.setVisibility(View.VISIBLE);
                } else {
                    totalFileDelete.setText("");
                    toolbarDelete.setVisibility(View.GONE);
                }
                Log.d("Selecttion", "" + selectionTracker.getSelection().toString());
            }
        });
        adapter.setSelectionTracker(selectionTracker);
    }

    private void setupViewModel() {
        realmViewModel = ViewModelProviders.of(requireActivity()).get(RealmViewModel.class);
        realmViewModel.getDownloadData().observe(getParentFragment().getViewLifecycleOwner(), data -> {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.setData(data);
                }
            });
            if (data != null && data.size() > 0) {
                nodata.setVisibility(View.GONE);
                item.setVisibility(View.VISIBLE);
            } else {
                nodata.setVisibility(View.VISIBLE);
                item.setVisibility(View.GONE);
            }
        });
    }

    private void setupUI(View view) {
        recyclerView = view.findViewById(R.id.listDownload);
        nodata = view.findViewById(R.id.noData);
        item = view.findViewById(R.id.itemFile);
        closeDeleteItem = view.findViewById(R.id.closeDeleteItem);
        btnDeleteFile = view.findViewById(R.id.btnDeleteFile);
        toolbarDelete = view.findViewById(R.id.toolbarDelete);
        totalFileDelete = view.findViewById(R.id.totalFileDelete);
        dowmloadNow = view.findViewById(R.id.dowmloadNow);
    }

    private void setupReycircleView() {
        linearLayoutManager = new LinearLayoutManager(getContext());
        adapter = new GaleryAdapter(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setPopupmenuListener(hendlePopupWindow);
    }

    private PopupmenuListener hendlePopupWindow = new PopupmenuListener() {
        @Override
        public void onDelete(long id) {
            realmViewModel.singleRemoveData(id);
        }
        @Override
        public void onShare(String filePath) {
            Log.d("Galery onShare", ""+filePath);
            uiUtils.shareFile(filePath, getContext());
        }
        @Override
        public void onOpenLikee(String deeplink) {
            Log.d("Galery onOpenLike", ""+deeplink);
            uiUtils.openLikee(deeplink, getContext());
        }

        @Override
        public void onClickedItem(DownloadModel data) {
            Log.d("GaleryFragments", data.getFilePath());
            Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
            intent.putExtra("avatar", data.getAvatar());
            intent.putExtra("filePath", data.getFilePath());
            intent.putExtra("username", data.getUsername());
            intent.putExtra("deeplink", data.getDeeplink());
            intent.putExtra("caption", data.getCaption());
            getActivity().startActivity(intent);
        }
    };
}