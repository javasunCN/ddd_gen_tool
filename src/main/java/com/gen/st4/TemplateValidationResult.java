package com.gen.st4;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.io.Serializable;
import java.util.*;

/**
 * 模板校验结果
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateValidationResult implements Serializable {

    private String templatePath;
    private boolean valid = true;
    private String encoding;
    private long fileSize;
    private Date lastModified;
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private List<String> availableTemplates = new ArrayList<>();

    public void addError(String error) {
        this.errors.add(error);
        this.valid = false;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
}