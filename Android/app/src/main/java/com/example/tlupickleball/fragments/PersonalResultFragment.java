package com.example.tlupickleball.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.DateAdapter;
import com.example.tlupickleball.adapters.MatchResultAdapter;
import com.example.tlupickleball.model.DateItem;
import com.example.tlupickleball.model.MatchResult;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PersonalResultFragment extends Fragment {
    RecyclerView recyclerViewDates, recyclerViewMatches;
    DateAdapter dateAdapter;
    MatchResultAdapter matchAdapter;

    List<DateItem> dates;
    Map<String, List<MatchResult>> matchDataByDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_result, container, false);

        recyclerViewDates = view.findViewById(R.id.recyclerViewDates);
        recyclerViewMatches = view.findViewById(R.id.recyclerViewMatches);

        dates = generateDateList(30);
        matchDataByDate = getMockMatchData();

        List<MatchResult> defaultResults = matchDataByDate.getOrDefault(dates.get(0).getDateValue(), new ArrayList<>());
        matchAdapter = new MatchResultAdapter(defaultResults);
        recyclerViewMatches.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMatches.setAdapter(matchAdapter);

        dateAdapter = new DateAdapter(dates, selectedDate -> {
            List<MatchResult> filtered = matchDataByDate.getOrDefault(selectedDate.getDateValue(), new ArrayList<>());
            matchAdapter.updateData(filtered);
        });

        recyclerViewDates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewDates.setAdapter(dateAdapter);

        return view;
    }

    private List<DateItem> generateDateList(int daysBack) {
        List<DateItem> dateList = new ArrayList<>();
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd 'tháng' M", new Locale("vi"));
        SimpleDateFormat valueFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < daysBack; i++) {
            String display = displayFormat.format(calendar.getTime());
            String value = valueFormat.format(calendar.getTime());
            dateList.add(new DateItem(display, value));
            calendar.add(Calendar.DATE, -1);
        }
        return dateList;
    }

    private Map<String, List<MatchResult>> getMockMatchData() {
        Map<String, List<MatchResult>> map = new HashMap<>();

        map.put("2025-05-25", Arrays.asList(
                new MatchResult("A", "Đông", "2-0"),
                new MatchResult("ANH A", "Đông Quyên", "1-2")
        ));

        map.put("2025-05-24", Arrays.asList(
                new MatchResult("Vi", "Zed", "0-2")
        ));

        map.put("2025-05-23", Arrays.asList(
                new MatchResult("Lux", "Ahri", "2-1")
        ));

        return map;
    }
}