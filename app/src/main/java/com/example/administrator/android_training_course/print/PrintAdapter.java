package com.example.administrator.android_training_course.print;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by liuyong on 2018/1/26.
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class PrintAdapter extends PrintDocumentAdapter {

    private OnItemCountListener mOnItemCountListener;
    private PdfDocument mPdfDocument;
    private Context context;
    private int totalPages;
    private long startTimes, endTimes;
    private PageRange[] writtenPagesArray;//打印的总数组
    private PageRange[] newWrittenPagesArray;//这次打印的数组

    public PrintAdapter(OnItemCountListener getPrintItemCount, Context context, PageRange[] writtenPagesArray) {
        this.mOnItemCountListener = getPrintItemCount;
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
        mPdfDocument = new PrintedPdfDocument(context, newAttributes);
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

            PdfDocument.Page page = mPdfDocument.startPage(mPdfDocument.getPages().get(i));
            if (cancellationSignal.isCanceled()) {
                callback.onWriteCancelled();
                mPdfDocument.close();
                mPdfDocument = null;
                return;
            }
            drawPage(page);
            mPdfDocument.finishPage(page);
        }
        try {
            mPdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
        } catch (IOException e) {
            e.printStackTrace();
            callback.onWriteFailed(e.toString());
        } finally {
            mPdfDocument.close();
            mPdfDocument = null;
        }
        mPdfDocument.getPages();
        /**
         * 最后计算一下需要打印的数组，应该是总数组。
         * 这里就将当前数组打印了。
         */
        /*PageRange[] writtenPages = computeWrittenPages();
        callback.onWriteFinished(writtenPages);*/
        callback.onWriteFinished(newWrittenPagesArray);//将当前打印数组打印
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
        if (mOnItemCountListener != null) {
            int printItemCount = mOnItemCountListener.getPrintItemCount();
            return (int) Math.ceil(printItemCount / itemsPerPage);
        } else {
            return 1;
        }
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


    private PageRange[] computeWrittenPages() {
        return new PageRange[0];
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

    //定义监听者，用来回调我们打印的条目数，不过到现在也不知道这个条目数是什么
    public interface OnItemCountListener {
        int getPrintItemCount();
    }
}
