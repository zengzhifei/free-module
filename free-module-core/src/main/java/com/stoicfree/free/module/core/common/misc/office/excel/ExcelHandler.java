package com.stoicfree.free.module.core.common.misc.office.excel;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

import com.stoicfree.free.module.core.common.support.Func;
import com.stoicfree.free.module.core.common.support.Safes;
import com.stoicfree.free.module.core.common.support.ThreeTuple;
import com.stoicfree.free.module.core.common.util.LambdaUtils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;

/**
 * @author zengzhifei
 * @date 2023/3/21 22:57
 */
public class ExcelHandler {
    private final ExcelWriter writer;
    private final Configure configure;
    private final AtomicInteger rows = new AtomicInteger(0);

    public ExcelHandler(long rowAccessWindowSize) {
        if (rowAccessWindowSize > BigExcelWriter.DEFAULT_WINDOW_SIZE) {
            this.writer = ExcelUtil.getBigWriter((int) rowAccessWindowSize);
        } else {
            this.writer = ExcelUtil.getWriter(true);
        }

        this.configure = (Configure) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {Configure.class},
                (proxy, method, args) -> ReflectUtil.invoke(writer, method.getName(), Safes.of(args, new Object[0]))
        );
    }

    public static ExcelHandler build(long rowAccessWindowSize) {
        return new ExcelHandler(rowAccessWindowSize);
    }

    public ExcelHandler config(Consumer<Configure> consumer) {
        consumer.accept(configure);
        return this;
    }

    public ExcelHandler sheet(String sheet) {
        writer.setSheet(sheet);
        return this;
    }

    public ExcelHandler title(List<ThreeTuple<Integer, String, CellStyle>> titles) {
        int startCol = 0;
        for (ThreeTuple<Integer, String, CellStyle> title : titles) {
            int endCol = startCol + title.getFirst() - 1;
            if (title.getThird() != null) {
                writer.merge(0, 0, startCol, endCol, title.getSecond(), title.getThird());
            } else {
                writer.merge(0, 0, startCol, endCol, title.getSecond(), true);
            }
            startCol = startCol + title.getFirst();
        }
        writer.passCurrentRow();
        return this;
    }

    public ExcelHandler header(LinkedHashMap<String, String> header) {
        writer.setHeaderAlias(header).setOnlyAlias(true);
        return this;
    }

    public <E> ExcelHandler content(List<E> contents) {
        writer.write(contents, rows.getAndAdd(contents.size()) == 0);
        return this;
    }

    public void output(String filename) {
        writer.flush(FileUtil.file(filename));
        close();
    }

    public void output(OutputStream out) {
        writer.flush(out, true);
        close();
    }

    public void output(HttpServletResponse response, String filename) throws IOException {
        response.setContentType(writer.getContentType());
        response.setHeader("Content-Disposition", writer.getDisposition(filename, StandardCharsets.UTF_8));
        writer.flush(response.getOutputStream(), true);
        close();
    }

    private void close() {
        writer.close();
    }

    public static class TitleBuilder {
        private final List<ThreeTuple<Integer, String, CellStyle>> titles = new ArrayList<>();
        private CellStyle style = null;

        public static TitleBuilder of() {
            return new TitleBuilder();
        }

        public static TitleBuilder of(CellStyle style) {
            TitleBuilder builder = new TitleBuilder();
            builder.style = style;
            return builder;
        }

        public TitleBuilder group(int cols, String title) {
            titles.add(ThreeTuple.of(cols, title, style));
            return this;
        }

        public TitleBuilder group(int cols, String title, CellStyle style) {
            titles.add(ThreeTuple.of(cols, title, style));
            return this;
        }

        public List<ThreeTuple<Integer, String, CellStyle>> build() {
            return titles;
        }
    }

    public static class HeaderBuilder<E> {
        private final LinkedHashMap<String, String> header = new LinkedHashMap<>();

        public static <E> HeaderBuilder<E> of() {
            return new HeaderBuilder<E>();
        }

        public HeaderBuilder<E> head(Func<E, ?> filed) {
            String name = LambdaUtils.getFieldName(filed);
            header.put(name, name);
            return this;
        }

        public HeaderBuilder<E> head(Func<E, ?> filed, String alias) {
            String name = LambdaUtils.getFieldName(filed);
            header.put(name, alias);
            return this;
        }

        public LinkedHashMap<String, String> build() {
            return header;
        }
    }

    public interface Configure {
        void autoSizeColumnAll();

        void autoSizeColumn(int columnIndex);

        void autoSizeColumn(int columnIndex, boolean useMergedCells);

        void disableDefaultStyle();

        void setStyleSet(StyleSet styleSet);

        StyleSet getStyleSet();

        CellStyle getHeadCellStyle();

        CellStyle getCellStyle();

        void setFreezePane(int rowSplit);

        void setFreezePane(int colSplit, int rowSplit);

        void setColumnWidth(int columnIndex, int width);

        void setDefaultRowHeight(int height);

        void setRowHeight(int rownum, int height);

        void setStyle(CellStyle style, String locationRef);

        void setStyle(CellStyle style, int x, int y);

        void setRowStyle(int y, CellStyle style);

        void setRowStyleIfHasData(int y, CellStyle style);

        void setColumnStyle(int x, CellStyle style);

        void setColumnStyleIfHasData(int x, int y, CellStyle style);

        Font createFont();

        void writeImg(File imgFile, int col1, int row1, int col2, int row2);
    }
}
