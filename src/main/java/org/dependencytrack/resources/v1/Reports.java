package org.dependencytrack.resources.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.dependencytrack.report.ExcelReportGenerator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
	
/**
 * JAX-RS resources for project report in excel format.
 *
 * @author Ravi Soni
 * @since 4.0.0
 */

@Path("/v1/report")
@Api(value = "report", authorizations = @Authorization(value = "X-Api-Key"))
public class Reports {

	@GET
    @Path("/project/{uuid}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
    @ApiOperation(
            value = "Get a project report",
            code = 204
    )
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "The UUID of the project could not be found")
    })
    public Response getProjectReport(
    		@ApiParam(value = "The UUID of the project to get report", required = true)
    		@PathParam("uuid") String uuid) {
		ExcelReportGenerator reportGenerator = new ExcelReportGenerator(uuid);
		byte[] excelReport = reportGenerator.generateProjectReport();
    	return Response.ok(excelReport, MediaType.APPLICATION_OCTET_STREAM)
    			.header("Content-Disposition", "attachment; filename=\"" + reportGenerator.getReportFileName() + "\"")
    			.build();
    }
}
