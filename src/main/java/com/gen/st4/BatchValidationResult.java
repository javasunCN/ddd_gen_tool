package com.gen.st4;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.io.Serializable;
import java.util.*;

/**
 * 批量校验结果
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchValidationResult implements Serializable {

    private int totalFiles = 0;
    private int validFiles = 0;
    private int invalidFiles = 0;
    private int filesWithWarnings = 0;
    private long totalDuration = 0;
    private Date validationTime = new Date();
    private List<TemplateValidationResult> results = new ArrayList<>();
    private Map<String, Object> summary = new HashMap<>();

    public void addResult(TemplateValidationResult result) {
        this.results.add(result);
        this.totalFiles++;

        if (result.isValid()) {
            this.validFiles++;
        } else {
            this.invalidFiles++;
        }

        if (result.hasWarnings()) {
            this.filesWithWarnings++;
        }
    }

    public List<TemplateValidationResult> getInvalidResults() {
        List<TemplateValidationResult> invalid = new ArrayList<>();
        for (TemplateValidationResult result : results) {
            if (!result.isValid()) {
                invalid.add(result);
            }
        }
        return invalid;
    }

    public List<TemplateValidationResult> getResultsWithWarnings() {
        List<TemplateValidationResult> withWarnings = new ArrayList<>();
        for (TemplateValidationResult result : results) {
            if (result.hasWarnings()) {
                withWarnings.add(result);
            }
        }
        return withWarnings;
    }

    public void calculateSummary() {
        summary.put("totalFiles", totalFiles);
        summary.put("validFiles", validFiles);
        summary.put("invalidFiles", invalidFiles);
        summary.put("filesWithWarnings", filesWithWarnings);
        summary.put("validPercentage", totalFiles > 0 ? (validFiles * 100.0 / totalFiles) : 0);
        summary.put("invalidPercentage", totalFiles > 0 ? (invalidFiles * 100.0 / totalFiles) : 0);
        summary.put("totalDuration", totalDuration);
        summary.put("averageDuration", totalFiles > 0 ? (totalDuration * 1.0 / totalFiles) : 0);
    }
}
