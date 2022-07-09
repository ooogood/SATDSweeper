package fx.satds_fx;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import commentparser.marker.CommentMarkerParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;

public class ReportWriter {
	/* page setting */
	private final PDRectangle pageSize = PDRectangle.A4;
	private final float pageH = pageSize.getHeight();
	private final float pageW = pageSize.getWidth();
	private final float margin = 50;
	private final float tabW = 10;
	/* text size and format */
	private final PDType1Font titleFont = PDType1Font.TIMES_ROMAN;
	private final PDType1Font headerFont = PDType1Font.TIMES_ROMAN;
	private final PDType1Font labelFont = PDType1Font.TIMES_BOLD;
	private final PDType1Font contentFont = PDType1Font.TIMES_ROMAN;
	private final int titleFontSize = 18;
	private final int titleDateFontSize = 6;
	private final int headerFontSize = 12;
	private final int labelFontSize = 8;
	private final int contentFontSize = 8;
	private final float lineSpacing = 4;
	private final float groupSpacing = 3 * lineSpacing;

	// multiply by sizeFactor: turn page width value into Font width value
	private final float contentSizeFactor = (float)(1000.0 / contentFontSize);
	private final float titleSizeFactor = (float)(1000.0 / titleFontSize);
	private final float headerSizeFactor = (float)(1000.0 / headerFontSize);
	/* table setting */
	private final float colCommentW = 210;
	private final float colLocationW = 100;
	private final float colAuthorW = 55;
	private final float colDateW = 40;
	private final float colPrioW = 20;
	private final float colEstTimeW = 50;
	private final int approxMaxCharComment;
	private final int approxMaxCharLocation;
	private final int approxMaxCharAuthor;
	private final int approxMaxCharDate;
	private final int approxMaxCharPrio;
	private final int approxMaxCharEstTime;
	private final float colSpacing = 6;
	private final String exceedRep = "...";
	/* writing cursor */
	private float curX;
	private float curY;
	PDDocument doc;
	PDPageContentStream curPageStream;
	private int curSize;
	private PDType1Font curFont;
	/* counters */
	private int totalSATDCount;
	private int totalMCount;
	private int totalSCount;
	private int totalCCount;
	private int totalWCount;
	private int totalUnratedCount;
	private double totalEstTime;

	public ReportWriter() {
		// must assign initial value to these
		// otherwise cannot new page.
		curFont = titleFont;
		curSize = titleFontSize;
		// calculate maximum maximum char count with whitespace 
		// (assuming whitespace is the smallest char in all font)
		float contentSpaceWidth = contentFont.getSpaceWidth() / contentSizeFactor;
		approxMaxCharComment = (int)( colCommentW / contentSpaceWidth );
		approxMaxCharLocation = (int)( colLocationW / contentSpaceWidth );
		approxMaxCharAuthor = (int)( colAuthorW / contentSpaceWidth );
		approxMaxCharDate = (int)( colDateW / contentSpaceWidth );
		approxMaxCharPrio = (int)( colPrioW / contentSpaceWidth );
		approxMaxCharEstTime = (int)( colEstTimeW / contentSpaceWidth );
	}

	public void write( String path ) throws IOException {
		initialiseCounters();

		doc = new PDDocument();
		newPage();

		curX = margin;
		curY = pageH - margin;

		// todo: maybe wrap adding title into a function
		// add report title
		String title = "Self-Admitted Technical Debt Report";
		changeFont(titleFont, titleFontSize);
		float titleWidth = titleFont.getStringWidth(title) / titleSizeFactor;
		curX = (float)(( pageW - titleWidth ) * 0.5);
		writeSingleLine(title);
		// add date beside report title
		changeFont(titleFont, titleDateFontSize);
		SimpleDateFormat formatter= new SimpleDateFormat( "yyyy-MM-dd" );
		String titleDate = formatter.format( new Date( System.currentTimeMillis() ) );
		curX = (float)(( pageW + titleWidth ) * 0.5 + 10.0);
		writeSingleLine(titleDate);

		// start writing content
		Set<String> kwSet = Model.getInst().getDB().getKeywordSet();
		if( kwSet.contains(CommentMarkerParser.DEFAULT_MARKER)) {
			// first write auto-generated SATDs
			writeKeywordGroup(CommentMarkerParser.DEFAULT_MARKER);
		}
		// write other keyword groups
		for( String kw : kwSet) {
			if( kw.equals(CommentMarkerParser.DEFAULT_MARKER)) continue;
			writeKeywordGroup( kw );
		}

		// write summary
		writeSummary();

		// close current page and save the document
		curPageStream.stroke();
		curPageStream.close();
		doc.save( path );
		doc.close();
	}

	protected void writeKeywordGroup( String kw ) throws IOException {
		CommentDB db = Model.getInst().getDB();
		Set<Comment> cms = db.getKeywordGroup(kw);
		if( cms == null || cms.size() == 0 ) return;

		// separate each group with spacing
		advanceHeight( groupSpacing );
		if( kw.equals(CommentMarkerParser.DEFAULT_MARKER) )
			writeHeader( "System Identified SATDs" );
		else
			writeHeader( "Selected by Keyword: " + kw );
		writeLabels();
		for( Comment cm : cms ) {
			if( cm.getMark().isSelected() ) {
				writeTableSingleRow( cm );
				addCommentIntoCount( cm );
			}
		}
	}

	protected void writeTableSingleRow( Comment cmt ) throws IOException {
		changeFont(contentFont, contentFontSize);
		// new line for content
		curX = margin;
		advanceHeight( curSize + lineSpacing );
		// write content (remove special characters in it first!)
		String trimedContent = fitStringIntoWidth( removeSpecialChar( cmt.getContent() ), colCommentW, approxMaxCharComment, contentFont, contentSizeFactor );
		writeSingleLine(trimedContent);
		curX += ( colCommentW + colSpacing );
		// write location
		String trimedLocation = fitStringIntoWidth(cmt.getLocation(), colLocationW, approxMaxCharLocation, contentFont, contentSizeFactor );
		writeSingleLine(trimedLocation);
		curX += ( colLocationW + colSpacing );
		// write author
		String trimedAuthor = fitStringIntoWidth(cmt.getAuthor(), colAuthorW, approxMaxCharAuthor, contentFont, contentSizeFactor );
		writeSingleLine(trimedAuthor);
		curX += ( colAuthorW + colSpacing );
		// write date
		String trimedDate = fitStringIntoWidth(cmt.getDate(), colDateW, approxMaxCharDate, contentFont, contentSizeFactor );
		writeSingleLine(trimedDate);
		curX += ( colDateW + colSpacing );
		// write priority (assume will not exceed width, it has only one char)
		writeSingleLine(String.valueOf( cmt.getPriority().getValue() ));
		curX += ( colPrioW + colSpacing );
		// write est. time
		String trimedEstTime = fitStringIntoWidth(cmt.getEstimate().getText(), colEstTimeW, approxMaxCharEstTime, contentFont, contentSizeFactor );
		writeSingleLine(trimedEstTime);
	}

	protected String fitStringIntoWidth( String src, float w, int approxMax, PDType1Font fnt, float sizeFactor ) throws IOException {
		// turn width into StringWidth
		w *= sizeFactor;
		if( fnt.getStringWidth( src ) <= w ) return src;
		w -= fnt.getStringWidth( exceedRep );
		// assert: w should not be negative
		// find how much width has src[0:approxMax) exceed
		if( approxMax > src.length() ) approxMax = src.length();
		float exceedW = fnt.getStringWidth( src.substring( 0, approxMax ) ) - w;
		int i = approxMax - 1;
		StringBuilder sb = new StringBuilder();
		sb.append( src.charAt( i-- ) );
		while( fnt.getStringWidth( sb.toString() ) < exceedW ) {
			sb.append( src.charAt( i-- ) );
		}
		return src.substring( 0, i + 1 ) + exceedRep;
	}

	protected void advanceHeight( float by ) throws IOException {
		if( curY - by < margin ) {
			newPage();
			curY = pageH - margin;
		}
		else curY -= by;
	}
	
	protected void newPage() throws IOException {
		if( curPageStream != null ) {
			curPageStream.stroke();
			curPageStream.close();
		}
		PDPage curPage = new PDPage( pageSize );
		doc.addPage(curPage);
		curPageStream = new PDPageContentStream(doc, curPage);
		curPageStream.setStrokingColor(Color.DARK_GRAY);
		curPageStream.setLineWidth(1);
		changeFont( curFont, curSize );
	}

	protected void changeFont( PDType1Font font, int size ) throws IOException {
		// assuming all font in the report is the same
		curSize = size;
		curFont = font;
		curPageStream.setFont( curFont, curSize );
	}

	protected void writeSingleLine( String text )
	throws IOException {
		curPageStream.beginText();
		curPageStream.newLineAtOffset( curX, curY );
		curPageStream.showText( text );
		curPageStream.endText();
	}

	protected void writeHeader( String text ) throws IOException {
		changeFont(headerFont, headerFontSize);
		// newline for header
		curX = margin;
		advanceHeight( curSize + lineSpacing );
		writeSingleLine( text );
	}

	protected void writeLabels() throws IOException {
		changeFont(labelFont, labelFontSize);
		// new line for content
		curX = margin;
		advanceHeight( curSize + lineSpacing );
		// write cotent
		String trimedContent = fitStringIntoWidth( "Comment", colCommentW, approxMaxCharComment, labelFont, contentSizeFactor );
		writeSingleLine(trimedContent);
		curX += ( colCommentW + colSpacing );
		// write location
		String trimedLocation = fitStringIntoWidth( "Location", colLocationW, approxMaxCharLocation, labelFont, contentSizeFactor );
		writeSingleLine(trimedLocation);
		curX += ( colLocationW + colSpacing );
		// write author
		String trimedAuthor = fitStringIntoWidth( "Author", colAuthorW, approxMaxCharAuthor, labelFont, contentSizeFactor );
		writeSingleLine(trimedAuthor);
		curX += ( colAuthorW + colSpacing );
		// write date
		String trimedDate = fitStringIntoWidth( "Since", colDateW, approxMaxCharDate, labelFont, contentSizeFactor );
		writeSingleLine(trimedDate);
		curX += ( colDateW + colSpacing );
		// write priority (assume will not exceed width, it has only one char)
		String trimedPrio = fitStringIntoWidth( "Prio.", colPrioW, approxMaxCharPrio, labelFont, contentSizeFactor );
		writeSingleLine(trimedPrio);
		curX += ( colPrioW + colSpacing );
		// write est. time
		String trimedEstTime = fitStringIntoWidth("Est. Days", colEstTimeW, approxMaxCharEstTime, labelFont, contentSizeFactor );
		writeSingleLine(trimedEstTime);
	}

	public static String removeSpecialChar(String test) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < test.length(); i++) {
            if (WinAnsiEncoding.INSTANCE.contains(test.charAt(i))) {
                b.append(test.charAt(i));
            }
        }
        return b.toString();
    }

	protected void initialiseCounters() {
		// initialise counters
		totalSATDCount = 0;
		totalMCount = 0;
		totalSCount = 0;
		totalCCount = 0;
		totalWCount = 0;
		totalUnratedCount = 0;
		totalEstTime = 0.0;
	}

	protected void addCommentIntoCount( Comment cm ) {
		++totalSATDCount;
		try {
			totalEstTime += Double.valueOf(cm.getEstimate().getText());
		} catch (NumberFormatException e) {
			// if cannot parse, do nothing
		}
		Character prio = cm.getPriority().getValue();
		if( prio.equals('M') )
			++totalMCount;
		else if( prio.equals('S') )
			++totalSCount;
		else if( prio.equals('C') )
			++totalCCount;
		else if( prio.equals('W') )
			++totalWCount;
		else
			++totalUnratedCount;
	}

	protected void writeSummary() throws IOException {
		advanceHeight( groupSpacing );
		writeHeader( "Summary" );

		changeFont(contentFont, contentFontSize);
		// new line for content
		curX = margin;
		advanceHeight( curSize + lineSpacing );
		writeSingleLine( "Total issues: " + String.valueOf(totalSATDCount) );
		curX = margin + tabW;
		advanceHeight( curSize + lineSpacing );
		writeSingleLine( "Must have issues: " + String.valueOf(totalMCount) );
		advanceHeight( curSize + lineSpacing );
		writeSingleLine( "Should have issues: " + String.valueOf(totalSCount) );
		advanceHeight( curSize + lineSpacing );
		writeSingleLine( "Could have issues: " + String.valueOf(totalCCount) );
		advanceHeight( curSize + lineSpacing );
		writeSingleLine( "Would like to have issues: " + String.valueOf(totalWCount) );
		advanceHeight( curSize + lineSpacing );
		writeSingleLine( "Unprioritised issues: " + String.valueOf(totalUnratedCount) );

		curX = margin;
		advanceHeight( curSize + lineSpacing );
		writeSingleLine( "Total estimated time to finish: " + String.valueOf(totalEstTime) + " day(s)" );

	}
}
