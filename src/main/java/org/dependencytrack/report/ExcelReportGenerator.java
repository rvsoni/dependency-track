package org.dependencytrack.report;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dependencytrack.model.Component;
import org.dependencytrack.model.Project;
import org.dependencytrack.model.Vulnerability;
import org.dependencytrack.persistence.QueryManager;

public class ExcelReportGenerator {

	private String uuid;
	
	private String reportFileName;
	
	public ExcelReportGenerator(String uuid) {
		super();
		this.uuid = uuid;
	}

	public byte[] generateProjectReport() {
		
		byte[] byteArray = null;
		final Workbook workbook; 
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			workbook = new XSSFWorkbook();
			
			CreationHelper createHelper = workbook.getCreationHelper();
			
			try (QueryManager qm = new QueryManager()) {
				final Project project = qm.getObjectByUuid(Project.class, uuid, Project.FetchGroup.ALL.name());
				this.reportFileName = project.getName() + ".xlsx";
				Sheet dependenciesSheet = workbook.createSheet("Dependencies");
				Sheet dependenciesVSheet = workbook.createSheet("Dependencies Vulnerabilities");
				List<Component> allComponents = qm.getAllComponents(project);
				int rowNumComponent = 1;
				int rowNumComponentV = 1; 
				for (Component component : allComponents) {
					int col = 0;
					Row rowComponent = dependenciesSheet.createRow(rowNumComponent++);
					rowComponent.createCell(col++).setCellValue(component.getPublisher());
					rowComponent.createCell(col++).setCellValue(component.getGroup());
					rowComponent.createCell(col++).setCellValue(component.getName());
					rowComponent.createCell(col++).setCellValue(component.getVersion());
					//rowComponent.createCell(col++).setCellValue(component.getRepositoryMeta().getLatestVersion());
					rowComponent.createCell(col++).setCellValue(component.getClassifier().name());
					rowComponent.createCell(col++).setCellValue(component.getLicense());
					rowComponent.createCell(col++).setCellValue(component.getPurl().toString());
					rowComponent.createCell(col++).setCellValue(component.getDescription());
					
					if(CollectionUtils.isNotEmpty(component.getVulnerabilities())) {
						for (Vulnerability vulnerability : component.getVulnerabilities()) {
							int colV = 0;
							Row rowComponentV = dependenciesVSheet.createRow(rowNumComponentV++);
							rowComponentV.createCell(colV++).setCellValue(component.getGroup());
							rowComponentV.createCell(colV++).setCellValue(component.getName());
							rowComponentV.createCell(colV++).setCellValue(component.getVersion());
							rowComponentV.createCell(colV++).setCellValue(vulnerability.getSource());
							rowComponentV.createCell(colV++).setCellValue(vulnerability.getVulnId());
							rowComponentV.createCell(colV++).setCellValue(vulnerability.getSeverity().toString());
							//rowComponentV.createCell(colV++).setCellValue(vulnerability.getCwe().getName());
							rowComponentV.createCell(colV++).setCellValue(vulnerability.getDescription());
							
						}
					}
				}
			}

			workbook.write(baos);
			workbook.close();	
			byteArray = baos.toByteArray();
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return byteArray;
	}

	public String getReportFileName() {
		return reportFileName;
	}
}
