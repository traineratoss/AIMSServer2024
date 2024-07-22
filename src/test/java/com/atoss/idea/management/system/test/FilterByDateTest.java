package com.atoss.idea.management.system.test;

import com.atoss.idea.management.system.service.IdeaService;
import com.atoss.idea.management.system.service.implementation.IdeaServiceImpl;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FilterByDateTest {

    @Mock
    private Root<?> root;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @InjectMocks
    private IdeaServiceImpl ideaService;

    @Mock
    private SimpleDateFormat simpleDateFormat;


    @Mock
    private Predicate predicate;

    @Mock
    private ParseException e;

    @BeforeEach
    private void setup() {
        MockitoAnnotations.openMocks(this);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Test
    public void filterByDateTest_fromDate() throws ParseException {
        String selectedDateFrom = "2024-07-15 ";
        String columnName = "date";

        Date fromDate = simpleDateFormat.parse(selectedDateFrom + "00:00:00");
        when(criteriaBuilder.greaterThanOrEqualTo(root.get(columnName), fromDate)).thenReturn(predicate);

        List<Predicate> predicatesList = ideaService.filterByDate(selectedDateFrom, null, root, criteriaBuilder, columnName);

        assertEquals(predicate, predicatesList.get(0));

        verify(criteriaBuilder, times(1)).greaterThanOrEqualTo(root.get(columnName), fromDate);
    }

    @Test
    public void filterByDateTest_toDate() throws ParseException {
        String selectedDateTo = "2024-07-01 ";
        String columnName = "date";

        Date toDate = simpleDateFormat.parse(selectedDateTo + "23:59:59");
        when(criteriaBuilder.lessThanOrEqualTo(root.get(columnName), toDate)).thenReturn(predicate);

        List<Predicate> predicatesList = ideaService.filterByDate(null, selectedDateTo, root, criteriaBuilder, columnName);

        assertEquals(predicate, predicatesList.get(0));

        verify(criteriaBuilder, times(1)).lessThanOrEqualTo(root.get(columnName), toDate);
    }

    @Test
    public void filterByDateTest_toAndFromDates() throws ParseException {
        String selectedDateFrom = "2024-07-01 ";
        String selectedDateTo = "2024-07-17 ";
        String columnName = "date";

        Date fromDate = simpleDateFormat.parse(selectedDateFrom + "00:00:00");
        Date toDate = simpleDateFormat.parse(selectedDateTo + "23:59:59");

        when(criteriaBuilder.between(root.get(columnName), fromDate, toDate)).thenReturn(predicate);

        List<Predicate> predicatesList = ideaService.filterByDate(selectedDateFrom, selectedDateTo, root, criteriaBuilder, columnName);

        assertEquals(predicate, predicatesList.get(0));

        verify(criteriaBuilder, times(1)).between(root.get(columnName), fromDate, toDate);
    }

    @Test
    public void filterByDateTest_Exception() throws ParseException{
        String selectedDateTo = "2024-07-01 ";
        String selectedDateFrom = "2024-07-17 ";
        String columnName = "date";

        try {
            when(simpleDateFormat.parse(selectedDateTo)).thenThrow(e);
            assertThrows(ParseException.class, () -> {
                ideaService.filterByDate(selectedDateFrom, selectedDateTo, root, criteriaBuilder, columnName);
            });
            verify(e, times(1)).printStackTrace();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void filterByDateTest_Exception1() throws ParseException{
        String selectedDateTo = "2024-07-01 ";
        String columnName = "date";

        try {
            when(simpleDateFormat.parse(selectedDateTo)).thenThrow(e);
            assertThrows(ParseException.class, () -> {
                ideaService.filterByDate(null, selectedDateTo, root, criteriaBuilder, columnName);
            });
            verify(e, times(1)).printStackTrace();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void filterByDateTest_Exception2() throws ParseException{
        String selectedDateFrom = "2024-07-17 ";
        String columnName = "date";

        try {
            when(simpleDateFormat.parse(selectedDateFrom)).thenThrow(ParseException.class);
            assertThrows(ParseException.class, () -> {
                ideaService.filterByDate(selectedDateFrom, null, root, criteriaBuilder, columnName);
            });
            verify(e, times(1)).printStackTrace();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }



}
