/*
 * Copyright (C) 2014-2018 SgrAlpha
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ingress.data.gdpr.parsers;

import static ingress.data.gdpr.parsers.utils.DataFileNames.FIELD_HELD_DAYS_TSV;
import static ingress.data.gdpr.parsers.utils.DataFileNames.LINK_HELD_DAYS_TSV;
import static ingress.data.gdpr.parsers.utils.DataFileNames.LINK_LENGTH__IN_KILOMETERS_TIMES_DAYS_HELD_TSV;
import static ingress.data.gdpr.parsers.utils.DataFileNames.MIND_UNITS_TIMES_DAYS_HELD_TSV;
import static ingress.data.gdpr.parsers.utils.DataFileNames.PORTAL_HELD_DAYS_TSV;
import static ingress.data.gdpr.parsers.utils.ErrorConstants.FILE_NOT_FOUND;

import ingress.data.gdpr.models.NumericBasedRecord;
import ingress.data.gdpr.models.reports.DefenseReport;
import ingress.data.gdpr.models.reports.ReportDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author SgrAlpha
 */
public class DefenseReportParser implements MultipleFilesParser<DefenseReport> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefenseReportParser.class);

    private static final ZonedDateTimeParser TIME_PARSER = ZonedDateTimeParser.getDefault();

    private final Executor executor;

    public DefenseReportParser() {
        final int cores = Runtime.getRuntime().availableProcessors();
        this.executor = new ThreadPoolExecutor(
                cores, cores,
                1, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(5)
        );
    }

    @Override public CompletableFuture<DefenseReport> parse(final List<Path> files) {
        final DefenseReport report = new DefenseReport();
        return CompletableFuture
                .allOf(
                        CompletableFuture
                                .supplyAsync(() -> {
                                    final ReportDetails<List<NumericBasedRecord<Double>>> details = parse(PORTAL_HELD_DAYS_TSV, files);
                                    report.setPortalHeldDays(details);
                                    return report;
                                }, executor),
                        CompletableFuture
                                .supplyAsync(() -> {
                                    final ReportDetails<List<NumericBasedRecord<Double>>> details = parse(LINK_HELD_DAYS_TSV, files);
                                    report.setLinkHeldDays(details);
                                    return report;
                                }, executor),
                        CompletableFuture
                                .supplyAsync(() -> {
                                    final ReportDetails<List<NumericBasedRecord<Double>>> details = parse(LINK_LENGTH__IN_KILOMETERS_TIMES_DAYS_HELD_TSV, files);
                                    report.setLinkLengthInKmTimesDaysHeld(details);
                                    return report;
                                }, executor),
                        CompletableFuture
                                .supplyAsync(() -> {
                                    final ReportDetails<List<NumericBasedRecord<Double>>> details = parse(FIELD_HELD_DAYS_TSV, files);
                                    report.setFieldHeldDays(details);
                                    return report;
                                }, executor),
                        CompletableFuture
                                .supplyAsync(() -> {
                                    final ReportDetails<List<NumericBasedRecord<Double>>> details = parse(MIND_UNITS_TIMES_DAYS_HELD_TSV, files);
                                    report.setMindUnitsTimesDaysHeld(details);
                                    return report;
                                }, executor)
                )
                .thenApplyAsync(unused -> report);
    }

    private static ReportDetails<List<NumericBasedRecord<Double>>> parse(final String targetFileName, final List<Path> files) {
        Optional<Path> dataFile = files.stream()
                .filter(file -> file.getFileName().toString().equals(targetFileName))
                .findFirst();
        if (!dataFile.isPresent()) {
            LOGGER.warn("Can not find report named '{}', skipping ...", targetFileName);
            return ReportDetails.error(FILE_NOT_FOUND);
        }
        final NumericBasedRecordParser<Double> parser = new NumericBasedRecordParser<>(TIME_PARSER, DoubleValueParser.getDefault());
        final ReportDetails<List<NumericBasedRecord<Double>>> details = parser.parse(dataFile.get());
        if (details.isOk()) {
            LOGGER.info("Parsed {} records in {}", details.getData().size(), targetFileName);
        } else {
            LOGGER.warn("Ran into error when parsing {}: {}", targetFileName, details.getError());
        }
        return details;
    }

}