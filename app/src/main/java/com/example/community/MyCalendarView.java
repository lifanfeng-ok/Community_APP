package com.example.community;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyCalendarView extends View implements View.OnTouchListener {

    private Date selectedStartDate; //选中开始的月
    private Date selectedEndDate; //选中结束的月
    private Date curDate; // 当前日历显示的月
    private Date today; // 今天的日期文字显示红色

    private Date downDate; // 手指按下状态时临时日期
    private Date showFirstDate, showLastDate; // 日历显示的第一个日期和最后一个日期
    private int downIndex = -1; // 按下的格子索引
    private Calendar calendar;
    private Surface surface;

    private int[] date = new int[42]; // 日历显示数字
    private int curStartIndex, curEndIndex; // 当前显示月的日历起始的索引
    private List<ClockStates> mLists = null;// 考勤状态集合 （年-月-日 1/2/3/4）

    //由于点击单元格我们要做相应的处理，所以这里做一个接口回调：
    private OnItemClickListener onItemClickListener;//日历格子的点击监听器

    // 给控件设置监听事件
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MyCalendarView(Context context) {
        super(context);
        init();
    }

    public MyCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    //初始化数据
    private void init() {
        curDate = selectedStartDate = selectedEndDate = today = new Date();//初始化
        calendar = Calendar.getInstance();//获取日历
        calendar.setTime(curDate);//日历设置当前月
        surface = new Surface();
        surface.density = getResources().getDisplayMetrics().density;//取屏幕的密度
        setBackgroundColor(surface.bgColor);
        setOnTouchListener(this);
    }

    //测量控件
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        surface.width = getResources().getDisplayMetrics().widthPixels;//整个控件的宽度
        surface.height = (int) (getResources().getDisplayMetrics().heightPixels * 1 / 2);//控件的高度取屏幕高度的一半
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(surface.width, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(surface.height, MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            surface.init();
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    //画图
    @Override
    protected void onDraw(Canvas canvas) {
        // 画 星期（日 一 二……）
        float weekTextY = surface.monthHeight + surface.weekHeight * 3 / 5f;
        for (int i = 0; i < surface.weekText.length; i++) {
            float weekTextX = i * surface.cellWidth
                    + (surface.cellWidth - surface.weekPaint.measureText(surface.weekText[i])) / 2f;
            canvas.drawText(surface.weekText[i], weekTextX, weekTextY, surface.weekPaint);
        }
        // 计算日期
        calculateDate();

        // 按下状态，选择状态背景色
        drawDownOrSelectedBg(canvas);

        //单元格内的文字绘制
        int todayIndex = -1;
        calendar.setTime(curDate);
        String curYearAndMonth = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH);//当前年月
        calendar.setTime(today);
        String todayYearAndMonth = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH);//今天所在的年月
        if (curYearAndMonth.equals(todayYearAndMonth)) {
            int todayNumber = calendar.get(Calendar.DAY_OF_MONTH);//获取 今天的日期（日）
            todayIndex = curStartIndex + todayNumber - 1;//今天日期的索引
        }

        int stateIndex = 0;//记录当月 有状态的索引
        String signText = "";//考勤的标记
        int signColor = 0;//考勤标记的颜色
        for (int i = 0; i < 42; i++) {
            int tColor = Color.parseColor("#000000");// 文字颜色
            if (todayIndex != -1 && i == todayIndex) {// 设置选中当天的背景
                if (todayIndex == downIndex || downIndex == -1) {
                    tColor = Color.parseColor("#ffffff");//今天的日期 刚好是你按下时的日期
                }
            }

            //把日历当前月之外的日期设置为灰暗色
            if (isLastMonth(i)) {// 上一个月的日期颜色
                tColor = surface.borderColor;
            } else if (isNextMonth(i)) {// 下一个月的日期颜色
                tColor = surface.borderColor;
            }

            //索引不是上一个月的,从本月第一天开始
            if (!isLastMonth(i) && !isNextMonth(i) && mLists != null && stateIndex < mLists.size()) {
                switch (Integer.parseInt(mLists.get(stateIndex).getDateType())) {
                    case 1://正常
                        signText = surface.signText[1];
                        signColor = surface.zcColor;
                        break;
                    case 2://异常
                        signText = surface.signText[2];
                        signColor = surface.ycColor;
                        break;
//                    case 3://休息
//                        signText = surface.signText[3];
//                        signColor = surface.xxColor;
//                        break;
//                    case 4://旷工
//                        signText = surface.signText[4];
//                        signColor = surface.kgColor;
//                        break;
                }
                //画 考勤状态标记
                drawSignText(canvas, i, signText, signColor);
                stateIndex++;
            }
            //画日期
            drawCellText(canvas, i, date[i] + "", tColor);
        }
        // 画边框
        canvas.drawPath(surface.boxPath, surface.borderPaint);
        super.onDraw(canvas);
    }

    //计算日期
    private void calculateDate() {
        calendar.setTime(curDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int monthStart = dayInWeek;
        if (monthStart == 1) {
            monthStart = 8;
        }
        monthStart -= 1; // 以日为开头-1，以星期一为开头-2
        curStartIndex = monthStart;
        date[monthStart] = 1;
        // last month
        if (monthStart > 0) {
            calendar.set(Calendar.DAY_OF_MONTH, 0);
            int dayInmonth = calendar.get(Calendar.DAY_OF_MONTH);
            for (int i = monthStart - 1; i >= 0; i--) {
                date[i] = dayInmonth;
                dayInmonth--;
            }
            calendar.set(Calendar.DAY_OF_MONTH, date[0]);
        }
        showFirstDate = calendar.getTime();
        // this month
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        // Log.d(TAG, "m:" + calendar.get(Calendar.MONTH) + " d:" +
        // calendar.get(Calendar.DAY_OF_MONTH));
        int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
        for (int i = 1; i < monthDay; i++) {
            date[monthStart + i] = i + 1;
        }
        curEndIndex = monthStart + monthDay;
        // next month
        for (int i = monthStart + monthDay; i < 42; i++) {
            date[i] = i - (monthStart + monthDay) + 1;
        }
        if (curEndIndex < 42) {
            // 显示了下一月的
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, date[41]);
        showLastDate = calendar.getTime();
    }

    /**
     * 画 单元格内的文字
     *
     * @param canvas
     * @param index
     * @param text
     */
    private void drawCellText(Canvas canvas, int index, String text, int color) {
        int x = getXByIndex(index);//列数
        int y = getYByIndex(index);//行数

        surface.datePaint.setColor(color);
        //月高+星期高+（行数-1）*单元格高+单元格高*0.75f
        float cellY = surface.monthHeight + surface.weekHeight + (y - 1) * surface.cellHeight
                + surface.cellHeight * 1 / 2f;
        //单元格宽 * （列数-1）+（单元格宽—文字宽）/2f
        float cellX = (surface.cellWidth * (x - 1)) + (surface.cellWidth - surface.datePaint.measureText(text)) / 2f;
        canvas.drawText(text, cellX, cellY, surface.datePaint);


    }

    /**
     * 画 单元格内的标记（正常  异常  休息……）
     *
     * @param canvas
     * @param index
     * @param text
     * @param color
     */
    private void drawSignText(Canvas canvas, int index, String text, int color) {
        int x = getXByIndex(index);//列数
        int y = getYByIndex(index);//行数
        surface.signPaint.setColor(color);
        //月高+星期高+（行数-1）*单元格高+单元格高*0.75f
        float cellY = surface.monthHeight + surface.weekHeight + (y - 1) * surface.cellHeight
                + surface.cellHeight * 1 / 2f + surface.cellHeight * 1 / 3;
        //单元格宽 * （列数-1）+（单元格宽—文字宽）/2f
        float cellX = (surface.cellWidth * (x - 1)) + (surface.cellWidth - surface.signPaint.measureText(text)) / 2f;
        canvas.drawText(text, cellX, cellY, surface.signPaint);
    }

    /**
     * 画格子的背景
     *
     * @param canvas
     * @param index
     * @param color
     */
    private void drawCellBg(Canvas canvas, int index, int color) {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.cellBgPaint.setColor(color);
        float left = surface.cellWidth * (x - 1) + surface.borderWidth - 1;
        float top = surface.monthHeight + surface.weekHeight + (y - 1) * surface.cellHeight + surface.borderWidth - 1;
        canvas.drawRect(left, top, left + surface.cellWidth - surface.borderWidth + 1,
                top + surface.cellHeight - surface.borderWidth + 1, surface.cellBgPaint);
    }

    /**
     * 把 一个月的考勤状态设置给日历
     *
     * @param lists
     */
    public void setClockStates(List<ClockStates> lists) {
        this.mLists = lists;
        invalidate();
    }

    //画 按下或者选择日期时的背景
    private void drawDownOrSelectedBg(Canvas canvas) {
        // 按下未抬起的背景
        if (downDate != null) {
            drawCellBg(canvas, downIndex, surface.cellDownColor);
        }
        // 选择的背景
        if (!selectedEndDate.before(showFirstDate) && !selectedStartDate.after(showLastDate)) {
            int[] section = new int[]{-1, -1};
            calendar.setTime(curDate);
            calendar.add(Calendar.MONTH, -1);
            findSelectedIndex(0, curStartIndex, calendar, section);
            if (section[1] == -1) {
                calendar.setTime(curDate);
                findSelectedIndex(curStartIndex, curEndIndex, calendar, section);
            }
            if (section[1] == -1) {
                calendar.setTime(curDate);
                calendar.add(Calendar.MONTH, 1);
                findSelectedIndex(curEndIndex, 42, calendar, section);
            }
            if (section[0] == -1) {
                section[0] = 0;
            }
            if (section[1] == -1) {
                section[1] = 41;
            }
            for (int i = section[0]; i <= section[1]; i++) {
                drawCellBg(canvas, i, surface.cellSelectedColor);
            }

        }
    }

    //查到 选中单元格的索引
    private void findSelectedIndex(int startIndex, int endIndex, Calendar calendar, int[] section) {
        for (int i = startIndex; i < endIndex; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, date[i]);
            Date temp = calendar.getTime();
            if (temp.compareTo(selectedStartDate) == 0) {
                section[0] = i;
            }
            if (temp.compareTo(selectedEndDate) == 0) {
                section[1] = i;
                return;
            }
        }
    }

    //获取 选中开始的月
    public Date getSelectedStartDate() {
        return selectedStartDate;
    }

    //获取 选中结束的月
    public Date getSelectedEndDate() {
        return selectedEndDate;
    }

    //当前点击的索引是不是上一个月的
    private boolean isLastMonth(int i) {
        if (i < curStartIndex) {
            return true;
        }
        return false;
    }

    //当前点击的索引是不是下一个月的
    private boolean isNextMonth(int i) {
        if (i >= curEndIndex) {
            return true;
        }
        return false;
    }

    //判断当前点击的格子（所在的年月）是不是上个月的
    public boolean isLastMonth(int year, int month) {
        calendar.setTime(curDate);
        int showYear = calendar.get(Calendar.YEAR);
        int showMonth = calendar.get(Calendar.MONTH) + 1;
        if (year == showYear && month == showMonth - 1) {
            return true;
        }
        if (year == showYear - 1 && year == 12) {
            return true;
        }

        return false;
    }

    //判断当前点击的格子（所在的年月）是不是下个月的
    public boolean isNextMonth(int year, int month) {
        calendar.setTime(curDate);
        int showYear = calendar.get(Calendar.YEAR);
        int showMonth = calendar.get(Calendar.MONTH) + 1;
        if (year == showYear && month == showMonth + 1) {
            return true;
        }
        if (year == showYear + 1 && year == 1) {
            return true;
        }

        return false;
    }

    //根据单元格的索引获取 所在的列数
    private int getXByIndex(int i) {
        return i % 7 + 1; // 1 2 3 4 5 6 7
    }

    //根据单元格所在的索引获取 所在的行数
    private int getYByIndex(int i) {
        return i / 7 + 1; // 1 2 3 4 5 6
    }

    /**
     * 获得当前应该显示的年月
     *
     * @return 格式：xxxx年x月
     */
    public String getYearAndmonth() {
        calendar.setTime(curDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        return year + "年" + month + "月";
    }

    /**
     * 获得当前应该显示的年月
     *
     * @return 格式：xxxx，x
     */
    public String getYearAndmonths() {
        calendar.setTime(curDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        StringBuffer str = new StringBuffer();
        str.append(year);
        str.append(",");
        str.append(month);
        return str.toString();
    }

    // 点击上一月
    public String clickLeftMonth() {
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, -1);
        curDate = calendar.getTime();
        invalidate();
        return getYearAndmonth();
    }

    // 点击下一月
    public String clickRightMonth() {
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, 1);
        curDate = calendar.getTime();
        invalidate();
        return getYearAndmonth();
    }

    /**
     * 跳转到指定的年月
     *
     * @param year  指定的年（格式：2017）
     * @param month 指定的月（格式：1,2……11,……）
     * @return 当前的月份  格式：2017年3月
     */
    public String clickSpecifiedMonth(int year, int month) {
        calendar.setTime(curDate);
        calendar.get(Calendar.YEAR);

        int addMonth = 0;//要跳转多少个月
        if (year == calendar.get(Calendar.YEAR)) {
            addMonth = month - calendar.get(Calendar.MONTH) - 1;
            calendar.add(Calendar.MONTH, addMonth);
        } else if (year < calendar.get(Calendar.YEAR)) {
            addMonth = (calendar.get(Calendar.YEAR) - year) * 12 + calendar.get(Calendar.MONTH) + 1 - month;
            calendar.add(Calendar.MONTH, -addMonth);
        } else {
            addMonth = (year - calendar.get(Calendar.YEAR)) * 12 + month - calendar.get(Calendar.MONTH) - 1;
            calendar.add(Calendar.MONTH, addMonth);
        }

        curDate = calendar.getTime();
        invalidate();
        return getYearAndmonth();
    }

    //设置选中的单元格
    private void setSelectedDateByCoor(float x, float y) {
        //按下日期单元格（不包括星期）
        if (y > surface.monthHeight + surface.weekHeight) {
            int m = (int) (Math.floor(x / surface.cellWidth) + 1);//单元格的列数
            //单元格的行数（不包括星期）
            int n = (int) (Math
                    .floor((y - (surface.monthHeight + surface.weekHeight)) / Float.valueOf(surface.cellHeight)) + 1);
            downIndex = (n - 1) * 7 + m - 1;//计算索引 从0开始记起
            calendar.setTime(curDate);
            if (isLastMonth(downIndex)) {
                calendar.add(Calendar.MONTH, -1);
            } else if (isNextMonth(downIndex)) {
                calendar.add(Calendar.MONTH, 1);
            }
            calendar.set(Calendar.DAY_OF_MONTH, date[downIndex]);
            downDate = calendar.getTime();
        }
        invalidate();
    }

    //触摸单元格的事件处理
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setSelectedDateByCoor(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                if (downDate != null) {
                    selectedStartDate = selectedEndDate = downDate;//选中开始的月=选中结束的月 = 按下时的月
                    // 响应监听事件
                    onItemClickListener.OnItemClick(selectedStartDate);
                    downDate = null;
                    invalidate();
                }
                break;
        }
        return true;
    }


    // 监听接口
    public interface OnItemClickListener {
        void OnItemClick(Date date);
    }

    /**
     * 外观：
     * 1. 布局尺寸 2. 文字颜色，大小 3. 当前日期的颜色，选择的日期颜色
     */
    private class Surface {

        public float density; //密度（设置边框宽度使用）
        public int width; // 整个控件的宽度
        public int height; // 整个控件的高度
        public float monthHeight; // 显示月的高度？

        public float weekHeight; // 显示星期的高度
        public float cellWidth; // 日期方框宽度
        public float cellHeight; // 日期单元格高度
        public float borderWidth;//边框的宽度


        public int bgColor = Color.parseColor("#FFFFFF");// 背景颜色
        // 原来的 日期 天数 的文字字体颜色 private int textColor = Color.BLACK;
        private int textColor = Color.parseColor("#999999");// Color.rgb(90, 90, 90);文字颜色
        private int btnColor = Color.parseColor("#666666");//

        private int borderColor = Color.parseColor("#CCCCCC");// 背景边框线

        public int cellDownColor = Color.parseColor("#FF9900");//格子按下时的颜色
        public int cellSelectedColor = Color.parseColor("#FF7700");// 格子 选中背景

        //画笔
        public Paint borderPaint;//边框画笔
        public Paint monthPaint;//画月  的画笔
        public Paint weekPaint;//画 星期的画笔
        public Paint datePaint;//画日期（日）的画笔
        public Paint monthChangeBtnPaint;//月份改变的画笔
        public Paint cellBgPaint;//格子背景的画笔
        public Path boxPath; // 边框路径
        public Paint signPaint;//标记画笔

        //数据
        public String[] weekText = {"日", "一", "二", "三", "四", "五", "六"};
        public String[] monthText = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
        public String[] signText = {"", "√", "x"};

        /**
         * 获取不同状态时的颜色值（用在日期下面标记的颜色）
         * 1.正常,2.异常,3.休息,4.旷工
         */
        public int zcColor = getResources().getColor(R.color.zc_clock);// 正常
        public int ycColor = getResources().getColor(R.color.yc_clock);// 异常
//        public int xxColor = getResources().getColor(R.color.xx_clock);// 休息
//        public int kgColor = getResources().getColor(R.color.kg_clock);// 旷

        //外观初始化
        public void init() {
            float temp = height / 7f;

            monthHeight = 0;
            weekHeight = (float) ((temp + temp * 0.3f) * 0.7);//星期的高度
            cellHeight = (height - monthHeight - weekHeight) / 6f;//日期（日）单元格的高度= 总-月-星期
            cellWidth = width / 7f;//单元格的宽度

            //画边框
            borderPaint = new Paint();
            borderPaint.setColor(borderColor);//设置边框的颜色
            borderPaint.setStyle(Paint.Style.STROKE);//设置画笔的样式：轮廓线
            borderWidth = (float) (0.5 * density);//边框的宽度
            borderWidth = borderWidth < 1 ? 1 : borderWidth;//边框的宽度跟屏幕适配
            borderPaint.setStrokeWidth(borderWidth);//设置边框的宽度

            //画月份
            monthPaint = new Paint();
            monthPaint.setColor(textColor);//月份文字的颜色
            monthPaint.setAntiAlias(true);//抗锯齿
            float textSize = cellHeight * 0.4f;//月 文字的大小（为单元格的0.4）
            monthPaint.setTextSize(textSize);//设置月 文字的大小

            //画 星期
            weekPaint = new Paint();
            weekPaint.setColor(textColor);//星期 的文字颜色
            weekPaint.setAntiAlias(true);
            float weekTextSize = weekHeight * 0.4f;
            weekPaint.setTextSize(weekTextSize);//设置星期 文字的大小？

            //画 日期（日）
            datePaint = new Paint();
            datePaint.setColor(textColor);//画笔颜色
            datePaint.setAntiAlias(true);//抗锯齿
            float cellTextSize = cellHeight * 0.4f;//单元格内文字的大小
            datePaint.setTextSize(cellTextSize);//设置文字大小

            //画 标记（“√”，“异常”，“休”，“旷”）
            signPaint = new Paint();

            signPaint.setAntiAlias(true);//抗锯齿
            float signTextSize = cellHeight * 0.2f;//标记的字体大小
            signPaint.setTextSize(signTextSize);//设置标记的大小

            //边框路径
            boxPath = new Path();
            boxPath.rLineTo(width, 0);
            boxPath.moveTo(0, monthHeight + weekHeight);
            boxPath.rLineTo(width, 0);
            for (int i = 1; i < 6; i++) {
                boxPath.moveTo(0, monthHeight + weekHeight + i * cellHeight);
                boxPath.rLineTo(width, 0);
                boxPath.moveTo(i * cellWidth, monthHeight);
                boxPath.rLineTo(0, height - monthHeight);
            }
            boxPath.moveTo(6 * cellWidth, monthHeight);
            boxPath.rLineTo(0, height - monthHeight);

            //月份改变的画笔
            monthChangeBtnPaint = new Paint();
            monthChangeBtnPaint.setAntiAlias(true);//抗锯齿
            monthChangeBtnPaint.setStyle(Paint.Style.FILL_AND_STROKE);//设置画笔的样式：同时绘制填充面和轮廓线
            monthChangeBtnPaint.setColor(btnColor);//单元格轮廓线的颜色

            //单元格背景的画笔
            cellBgPaint = new Paint();
            cellBgPaint.setAntiAlias(true);//抗锯齿
            cellBgPaint.setStyle(Paint.Style.FILL);//设置画笔的样式：绘制是填充面
            cellBgPaint.setColor(cellSelectedColor);//填充面的颜色
        }
    }
}


