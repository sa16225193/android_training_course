package com.example.administrator.android_training_course.print;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintJobInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.administrator.android_training_course.R;

import java.io.FileOutputStream;
import java.io.IOException;
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
                        new PrintAdapter(new getPrintItemCountListener() {
                            @Override
                            public int getPrintItemCount() {
                                return 2;
                            }
                        }), null);
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


    /**
     * 自定义打印适配器
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public class PrintAdapter extends PrintDocumentAdapter {
        private getPrintItemCountListener getPrintItemCount = null;
        private PdfDocument pdfDocument = null;
        private Context context;
        private int totalPages;
        private long startTimes, endTimes;
        private PageRange[] writtenPagesArray;//打印的总数组
        private PageRange[] newWrittenPagesArray;//这次打印的数组

        public PrintAdapter(getPrintItemCountListener getPrintItemCount) {
            this.getPrintItemCount = getPrintItemCount;
            this.context = context;
            this.writtenPagesArray = writtenPagesArray;
        }

        @Override
        public void onStart() {
            super.onStart();
            //初始化
            startTimes = System.currentTimeMillis();
            Log.i("xwq", "startTimes:" + startTimes);
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
            pdfDocument = new PrintedPdfDocument(context, newAttributes);
            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }
            totalPages = computePageCount(newAttributes);
            if (totalPages > 0) {
                newWrittenPagesArray = new PageRange[totalPages];
                PrintDocumentInfo printDocumentInfo = new PrintDocumentInfo.Builder("print_output.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalPages)
                        .build();
                callback.onLayoutFinished(printDocumentInfo, true);
            } else {
                callback.onLayoutFailed("Page count calculation failed.<=0");
            }
        }

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
            for (int i = 0; i < totalPages; i++) {
                if (containsPages(pages, i)) {
                    /**
                     * 这次打印的数组中，添加当前i页
                     */
                    for (int j = 0; j < newWrittenPagesArray.length; j++) {
                        if (newWrittenPagesArray[j] == null) {
                            newWrittenPagesArray[j] = pages[i];
                        }
                    }

                    /**
                     *当前i页添加进总数组中，这里写了这个方法，在外面就不用写追加的方法了。
                     * 这里就打印当前数组了，写死了。
                     */
                }

                PdfDocument.Page page = pdfDocument.startPage(pdfDocument.getPages().get(i));
                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    pdfDocument.close();
                    pdfDocument = null;
                    return;
                }
                drawPage(page);
                pdfDocument.finishPage(page);
            }
            try {
                pdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
            } catch (IOException e) {
                e.printStackTrace();
                callback.onWriteFailed(e.toString());
            } finally {
                pdfDocument.close();
                pdfDocument = null;
            }
            pdfDocument.getPages();
            /**
             * 最后计算一下需要打印的数组，应该是总数组。
             * 这里就将当前数组打印了。
             */
        /*PageRange[] writtenPages = computeWrittenPages();
        callback.onWriteFinished(writtenPages);*/
            callback.onWriteFinished(newWrittenPagesArray);//将当前打印数组打印
        }

        private PageRange[] computeWrittenPages() {
            return new PageRange[0];
        }

        private void drawPage(PdfDocument.Page page) {
            Canvas canvas = page.getCanvas();
            int titleBaseLine = 72;
            int leftMargin = 54;

            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setTextSize(36);
            canvas.drawText("Hello Title", leftMargin, titleBaseLine, paint);

            paint.setTextSize(11);
            canvas.drawText("Hello paragraph", leftMargin, titleBaseLine + 25, paint);

            paint.setColor(Color.BLUE);
            canvas.drawRect(100, 100, 172, 172, paint);
        }

        private boolean containsPages(PageRange[] pages, int i) {
            int mStart, mEnd;
            boolean isContain = false;
            for (int j = 0; j < pages.length; j++) {
                mStart = pages[j].getStart();
                mEnd = pages[j].getEnd();
                isContain = (i >= mStart) && (i <= mEnd);
                if (isContain)
                    return isContain;
            }
            return isContain;
        }

        @Override
        public void onFinish() {
            super.onFinish();
            endTimes = System.currentTimeMillis();
            Log.i("xwq", "endTimes:" + endTimes + " and the time between start and finish is :" + (endTimes - startTimes));
        }

        private int computePageCount(PrintAttributes printAttributes) {
            int itemsPerPage = 4;

            PrintAttributes.MediaSize pageSize = printAttributes.getMediaSize();
            if (!pageSize.isPortrait()) {
                itemsPerPage = 6;
            }
            if (getPrintItemCount != null) {
                int printItemCount = getPrintItemCount.getPrintItemCount();
                return (int) Math.ceil(printItemCount / itemsPerPage);
            } else {
                return 1;
            }
        }
    }

    //定义监听者，用来回调我们打印的条目数，不过到现在也不知道这个条目数是什么
    public interface getPrintItemCountListener {
        int getPrintItemCount();
    }
}
