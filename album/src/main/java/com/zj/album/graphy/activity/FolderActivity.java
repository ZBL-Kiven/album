package com.zj.album.graphy.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.zj.album.R;
import com.zj.album.graphy.PhotoFileHelper;
import com.zj.album.graphy.adapter.Pop_ChooseFileAdapter;
import com.zj.album.graphy.views.IRecyclerAdapter;
import com.zj.album.interfaces.PhotoEvent;

public class FolderActivity extends AppCompatActivity {

    private Pop_ChooseFileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        initView();
    }

    private void initView() {
        adapter = new Pop_ChooseFileAdapter(this, new IRecyclerAdapter.OnItemCLickListener() {
            @Override
            public void onItemClick(int postion, View view) {
                adapter.notifyDataSetChanged();
                PhotoFileHelper.getInstance().setCurFileInfo(adapter.getItem(postion), new PhotoEvent() {
                    @Override
                    public void onEvent(int code, boolean isValidate) {
                        Intent intent = new Intent();
                        intent.putExtra("code", code);
                        intent.putExtra("isValidate", isValidate);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                });
            }
        });
        adapter.add(PhotoFileHelper.getInstance().getFileInfos());
        ((ListView) findViewById(R.id.pop_lvFile)).setAdapter(adapter);
        findViewById(R.id.folder_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
