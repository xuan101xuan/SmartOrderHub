package com.example.soh.Utils;

import android.text.InputFilter;
import android.text.Spanned;

public class utilsInputFilter {
    // Phương thức tạo InputFilter cho việc giới hạn giá trị theo phạm vi cho trước
    public static InputFilter createInputFilter(final int minValue, final int maxValue) {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                try {
                    // Kết hợp giá trị hiện tại và giá trị mới
                    String newVal = dest.subSequence(0, dstart).toString() +
                            source.subSequence(start, end) +
                            dest.subSequence(dend, dest.length()).toString();

                    int input = Integer.parseInt(newVal);

                    // Kiểm tra xem giá trị có nằm trong phạm vi cho phép không
                    if (isInRange(input, minValue, maxValue)) {
                        return null; // Giá trị hợp lệ, không có sự thay đổi
                    }
                } catch (NumberFormatException ignored) {
                    // Nếu không phải là số nguyên, không làm gì cả
                }

                // Nếu giá trị không hợp lệ, trả về chuỗi trống để không thay đổi giá trị nhập vào
                return "";
            }

            // Phương thức kiểm tra xem giá trị có nằm trong phạm vi cho phép không
            private boolean isInRange(int c, int minValue, int maxValue) {
                return c >= minValue && c <= maxValue;
            }
        };
    }
}
