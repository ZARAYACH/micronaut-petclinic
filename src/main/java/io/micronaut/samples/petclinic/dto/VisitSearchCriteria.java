package io.micronaut.samples.petclinic.dto;

import java.time.LocalDate;

/**
 * Optional filters used by the visit search page.
 *
 * @param fromDate earliest visit date, inclusive
 * @param toDate latest visit date, inclusive
 * @param maxDurationMinutes exclusive upper bound for visit duration
 * @param maxFollowUpMonths exclusive upper bound for follow-up period
 */
public record VisitSearchCriteria(
        LocalDate fromDate,
        LocalDate toDate,
        Integer maxDurationMinutes,
        Integer maxFollowUpMonths
) {
}
