package crm.service;

import crm.dto.StatusResponse;

/**
 * Service interface for status checking operations.
 * Provides methods to check the status of API endpoints.
 */
public interface StatusCheckService {

    /**
     * Checks the status of the /api/book endpoint.
     * 
     * @return StatusResponse containing the status message and timestamp
     */
    StatusResponse checkApiStatus();
}
