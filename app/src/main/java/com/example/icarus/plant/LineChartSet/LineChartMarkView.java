package com.example.icarus.plant.LineChartSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.example.icarus.plant.R;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;

import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;
import java.util.List;

public class LineChartMarkView extends MarkerView {

    private TextView tvDate;
    private TextView tvValue;
    private IAxisValueFormatter xAxisValueFormatter;
    DecimalFormat df = new DecimalFormat(".00");

    public LineChartMarkView(Context context, IAxisValueFormatter xAxisValueFormatter) {
        super(context, R.layout.mark_view);
        this.xAxisValueFormatter = xAxisValueFormatter;
        tvDate = findViewById(R.id.tv_date);
        tvValue = findViewById(R.id.tv_value);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        //展示自定义X轴值 后的X轴内容
        tvDate.setText(xAxisValueFormatter.getFormattedValue(e.getX(), null));
        tvValue.setText("值：" + df.format(e.getY()));
        super.refreshContent(e, highlight);
    }
    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}

