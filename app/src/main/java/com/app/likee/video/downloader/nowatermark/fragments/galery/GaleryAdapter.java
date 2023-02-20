package com.app.likee.video.downloader.nowatermark.fragments.galery;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.app.likee.video.downloader.nowatermark.R;
import com.app.likee.video.downloader.nowatermark.database.RealmViewModel;
import com.app.likee.video.downloader.nowatermark.models.DownloadModel;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GaleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Activity activity;
    private static final int AD_TYPE = 1;
    private static final int CONTENT_TYPE = 0;
    private List<DownloadModel> listData = new ArrayList<>();
    private SelectionTracker<Long> selectionTracker;
    private List<Long> idSelect = new ArrayList<>();
    private List<ViewHolder> rView = new ArrayList<>();
    private PopupmenuListener popupmenuListener;


    public GaleryAdapter(Activity activity){
        this.activity = activity;
    }

    public void setPopupmenuListener(PopupmenuListener popupmenuListener) {
        this.popupmenuListener = popupmenuListener;
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    public void setData(List<DownloadModel> data){
        this.listData = data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView thumb;
        TextView username, caption;
        ImageButton  menuPopup;
        ImageView uncheckItem, checkItem;
        MaterialCardView wrapperRow;
        FrameLayout clickItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.thumb);
            username = itemView.findViewById(R.id.username);
            caption = itemView.findViewById(R.id.caption);
            wrapperRow = itemView.findViewById(R.id.wrapperRow);
            clickItem = itemView.findViewById(R.id.clickItem);
            uncheckItem = itemView.findViewById(R.id.uncheckItem);
            checkItem = itemView.findViewById(R.id.checkItem);
            menuPopup = itemView.findViewById(R.id.menuPopup);
        }

        public ItemDetailsLookup.ItemDetails<Long> getItemData(){
            return new MyItemDetail(getAdapterPosition(), listData.get(getAdapterPosition()).getId());
        }

        void bind(DownloadModel downloadModel, boolean isActived){
            username.setText("@"+downloadModel.getUsername());
            caption.setText(downloadModel.getCaption());
            if(downloadModel.getFilePath() != null || !downloadModel.getFilePath().equals("")){
                Glide.with(activity).asBitmap().load(downloadModel.getFilePath()).centerCrop().into(thumb);
            }else{
                Glide.with(activity).asBitmap().load(downloadModel.getThumb()).centerCrop().into(thumb);
            }

            menuPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    costumPopupMenu(menuPopup, downloadModel, getAdapterPosition());
                }
            });

            if(isActived){
                checkItem.setVisibility(View.VISIBLE);
            }else{
                checkItem.setVisibility(View.GONE);
                clickItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupmenuListener.onClickedItem(downloadModel);
                    }
                });
            }
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list_download_item, parent, false));
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DownloadModel downloadModel = listData.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.bind(downloadModel, selectionTracker.isSelected(downloadModel.getId()));

        idSelect.add(downloadModel.getId());
        rView.add(position, viewHolder);
    }

    @Override
    public int getItemCount() {
        if(listData != null){
            return listData.size();
        }
        return 0;
    }

    public void setCheckLayout(boolean isTrue){
        if(isTrue){
            for(ViewHolder v : rView){
                v.menuPopup.setVisibility(View.GONE);
                v.uncheckItem.setVisibility(View.VISIBLE);
            }
        }else{
            for(ViewHolder v : rView){
                v.menuPopup.setVisibility(View.VISIBLE);
                v.uncheckItem.setVisibility(View.GONE);
            }
        }
    }

    public Long getItem(int position){
        return listData.get(position).getId();
    }

    public int getPosition(Long key){
        return idSelect.indexOf(key);
    }

    public static class ItemKeyProvider extends androidx.recyclerview.selection.ItemKeyProvider<Long>{
        private final GaleryAdapter adapter;

        public ItemKeyProvider(GaleryAdapter adapter) {
            super(SCOPE_CACHED);
            this.adapter = adapter;
        }


        @Nullable
        @Override
        public Long getKey(int position) {
            return adapter.getItem(position);
        }

        @Override
        public int getPosition(@NonNull Long key) {
            return adapter.getPosition(key);
        }
    }

    public static class MyItemDetail extends ItemDetailsLookup.ItemDetails<Long>{

        private final int position;
        private final long itemId;

        MyItemDetail(int position, Long itemId) {
            this.position = position;
            this.itemId = itemId;
        }

        @Override
        public int getPosition() {
            return position;
        }

        @Nullable
        @Override
        public Long getSelectionKey() {
            return itemId;
        }
    }

    public static class MyItemLookup extends ItemDetailsLookup<Long>{
        private final RecyclerView recyclerView;

        public MyItemLookup(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Nullable
        @Override
        public ItemDetails getItemDetails(@NonNull MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if(view != null){
                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                if(viewHolder instanceof GaleryAdapter.ViewHolder){
                    return ((GaleryAdapter.ViewHolder) viewHolder).getItemData();
                }
            }
            return null;
        }
    }

    public void costumPopupMenu(View viewx, DownloadModel data, int position){
        LinearLayout copyText, share, repost, openIg, delete, copy;
        PopupWindow popupWindow = new PopupWindow();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.rv_popup_menu_layout, null, false);
        popupWindow.setFocusable(true);
        if(Build.VERSION.SDK_INT>=21){
            popupWindow.setElevation(5.0f);
        }
        popupWindow.setWidth(500);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);
        popupWindow.showAsDropDown(viewx, -430, -100);
        copyText = view.findViewById(R.id.copyText);
        share = view.findViewById(R.id.share);
        openIg = view.findViewById(R.id.openinig);
        delete = view.findViewById(R.id.delete);

        copyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PopupWindow", "Copy Button");
                ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Url Instagram", data.getUrlShare());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(activity.getApplicationContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupmenuListener.onShare(data.getFilePath());
                popupWindow.dismiss();
            }
        });

        openIg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupmenuListener.onOpenLikee(data.getDeeplink());
                popupWindow.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupmenuListener.onDelete(data.getId());
                popupWindow.dismiss();
            }
        });

    }
}
