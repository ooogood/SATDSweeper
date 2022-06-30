package fx.satds_fx;

import java.awt.*;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javafx.scene.text.Font;

public class ReportWriter {
	private PDType1Font font = PDType1Font.TIMES_ROMAN;
	public void write( String path ) throws IOException {
		// TODO: read from model and write to pdf

		// TODO: Add each item
		PDDocument doc = new PDDocument();
		PDPage first = new PDPage( PDRectangle.A4);
		int h = (int) first.getTrimBox().getHeight();
		int w = (int) first.getTrimBox().getWidth();
		doc.addPage(first);

		PDPageContentStream pds = new PDPageContentStream(doc, first);
		pds.setStrokingColor(Color.DARK_GRAY);
		pds.setLineWidth(1);

		
		int initX = 50;
		int initY = h - 50;
		int cellH = 30;
		int cellW = 100;
		int colCnt = 5;
		int rowCnt = 10;

		// add title
		String title = "Self-Admitted Technical Debt Report";
		int titleSize = 24; // titleSize
		pds.setFont(font, titleSize);
		float titleWidth = font.getStringWidth(title) / 1000 * titleSize;
		float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * titleSize;
		writeSingleLine(pds, title, (float)(( w - titleWidth ) * 0.5), initY);
		initY -= titleHeight;

		// add items
		pds.setFont(font, 14);
		for( int i = 0; i < rowCnt; ++i ) {
			for( int j = 0; j < colCnt; ++j ) {
				writeSingleLine(pds, "Hello", initX + 10, initY - cellH + 10);
				initX += cellW;
			}
			initX = 50;
			initY -= cellH;
		}
		pds.stroke();
		pds.close();

		doc.save( path );
		doc.close();
	}
	protected void writeSingleLine( PDPageContentStream pds, String text, float offsetX, float offsetY )
	throws IOException {
		pds.beginText();
		pds.newLineAtOffset( offsetX, offsetY );
		pds.showText( text );
		pds.endText();
	}
}
