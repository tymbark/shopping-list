package com.damianmichalak.shopping_list.helper;

import android.content.res.Resources;

import com.damianmichalak.shopping_list.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class DateHelper {

    @Nonnull
    private final Resources resources;

    private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private SimpleDateFormat sdfTimeWeek = new SimpleDateFormat("EEE HH:mm", Locale.getDefault());
    private SimpleDateFormat sdfTimeMonth = new SimpleDateFormat("d MMM HH:mm", Locale.getDefault());
    private SimpleDateFormat sdfTimeDateYear = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.getDefault());

    @Inject
    public DateHelper(@Nonnull Resources resources) {
        this.resources = resources;
    }

    public String getDateForTimestamp(long timestamp) {
        final Calendar now = Calendar.getInstance();
        final Calendar old = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        old.setTimeInMillis(timestamp);

        if (now.get(Calendar.YEAR) == old.get(Calendar.YEAR)) {
            if (now.get(Calendar.DAY_OF_YEAR) == old.get(Calendar.DAY_OF_YEAR)) {
                return resources.getString(R.string.date_today) + sdfTime.format(new Date(timestamp));
            } else if (now.get(Calendar.WEEK_OF_YEAR) == old.get(Calendar.WEEK_OF_YEAR)) {
                return sdfTimeWeek.format(new Date(timestamp));
            } else {
                return sdfTimeMonth.format(new Date(timestamp));
            }
        }

        return sdfTimeDateYear.format(new Date(timestamp));
    }

}
