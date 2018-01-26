package com.example.administrator.android_training_course.print;

import android.content.Context;
import android.os.Build;
import android.print.PageRange;
import android.print.PrintJob;
import android.print.PrintJobInfo;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.administrator.android_training_course.R;

import java.util.List;

/**
 * 打印自定义文档
 */
public class PrintActivity extends AppCompatActivity {

    private PrintManager mPrintManager;//打印管理器
    private List<PrintJob> mPrintJobList;
    private PageRange[] mWrittenPageRanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
    }

    public void print(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mPrintManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
            mPrintJobList = mPrintManager.getPrintJobs();
            if (!mPrintJobList.isEmpty()) {
                mWrittenPageRanges = mPrintJobList.get(0).getInfo().getPages();
            } else {
                mWrittenPageRanges = new PageRange[0];
                PrintJob printJob = mPrintManager.print("mDocument",
                        new PrintAdapter(new PrintAdapter.OnItemCountListener() {
                            @Override
                            public int getPrintItemCount() {
                                return 2;
                            }
                        }, this, mWrittenPageRanges), null);
                PrintJobInfo printJobInfo = printJob.getInfo();
                mPrintJobList.add(printJob);
                PageRange[] pageRanges = printJobInfo.getPages();
                if (mWrittenPageRanges.length == 0)
                    mWrittenPageRanges = pageRanges;
                else {
                    /**
                     * 在writtenPagesRanges后追加我们的pageRanges
                     *
                     * */
                }
            }
        }

    }

}
