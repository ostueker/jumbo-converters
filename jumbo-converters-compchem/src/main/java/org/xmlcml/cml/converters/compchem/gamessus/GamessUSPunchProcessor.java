package org.xmlcml.cml.converters.compchem.gamessus;

import java.util.List;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.LegacyProcessor;

/**
 *  $DATA  
HCO-L-Ala-NH2 - OPTIMIZE - B3LYP/cc-pVDZ - N_at = 16                            
C1       0
H1          1.0     -2.9805271364       .9147039208       .1059830464
   CCD     0
        
C2          6.0     -2.0999914660       .2907269834      -.0395326325
   CCD     0
        
...
H16         1.0      -.5854990105     -1.5842704744       .5426395725
   CCD     0
        
 $END      
 $ZMAT   IZMAT(1)=
         3,   2,   3,   5,   7,
         3,   3,   5,   7,  13,
         1,   1,   2,
         1,   2,   4,
         ...
         1,  13,  15,
         2,   1,   2,   3,
         2,   1,   2,   4,
...
         2,  15,  13,  16,
         3,   1,   2,   3,   6,
         3,   1,   2,   3,   5,
         ...
         3,  14,   7,  13,  16,
         3,  14,   7,  13,  15,
 $END

 * @author pm286
 *
 */
public class GamessUSPunchProcessor extends LegacyProcessor {

	private static final String MINUS3 = "---";
	public GamessUSPunchProcessor() {
	}
	
	@Override
	protected AbstractCommon getCommon() {
		return new GamessUSCommon();
	}

	/**
	 * @param lines
	 * @param lineCount
	 * @return
	 */
	@Override
	protected AbstractBlock readBlock(List<String> lines) {
		AbstractBlock block = null;
		String line = lines.get(lineCount);
		if (line.startsWith(GamessUSCommon.KEYWORD)) {
			block = createBlock();
		} else if (line.startsWith(MINUS3)) {
			block = createAnonymousBlock();
		} else {
			block = createAnonymousBlock();
		}
		block.convertToRawCML();
		return block;
	}

	private AbstractBlock createAnonymousBlock() {
		AbstractBlock block = new GamessUSPunchBlock(blockContainer);
		int lineCount0 = lineCount;
		while (lineCount < lines.size()) {
			String line = lines.get(lineCount);
			if (line.startsWith(GamessUSCommon.KEYWORD)) {
				break;
			} else if (lineCount > lineCount0 && line.startsWith(MINUS3)) {
				break;
			}
			block.add(line);
			lineCount++;
		}
		block.setBlockName(_ANONYMOUS);
		return block;
	}
	/**
	 * read block start , create name
	 * then read lines until next block start
	 * @return
	 */
	private AbstractBlock createBlock() {
		AbstractBlock block = new GamessUSPunchBlock(blockContainer);
		String line = lines.get(lineCount);
		String[] tokens = line.trim().split(CMLConstants.S_WHITEREGEX);
		block.setBlockName(tokens[0].substring(1).trim());
		lineCount++;
		while (lineCount < lines.size() && !lines.get(lineCount).startsWith(GamessUSCommon.END)) {
			block.add(lines.get(lineCount++));
		}
		lineCount++;
		return block;
	}
	
	@Override
	protected void preprocessBlocks() {
		// not required
	}
	@Override
	protected void postprocessBlocks() {
//		processAnonymousBlocks();
	}
	
}
