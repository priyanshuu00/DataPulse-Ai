package com.datapulse.ai.core.services;

import com.datapulse.ai.core.domain.DatasetVersion;

public class VersionManagementService {
    
    public String calculateNextVersion(DatasetVersion currentVersion, boolean isBreakingChange) {
        // Semantic versioning logic for Datasets
        if (currentVersion == null) {
            return "1.0.0";
        }
        
        String[] parts = currentVersion.semanticVersion().split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        
        if (isBreakingChange) {
            return (major + 1) + ".0.0";
        } else {
            return major + "." + (minor + 1) + ".0";
        }
    }
}
