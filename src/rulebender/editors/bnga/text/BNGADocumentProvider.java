package rulebender.editors.bnga.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

public class BNGADocumentProvider extends AbstractDocumentProvider 
{
	@Override
	protected IDocument createDocument(Object element) throws CoreException 
	{
		if (element instanceof IEditorInput) {
			IDocument document= new Document();
			if (setDocumentContent(document, (IEditorInput) element)) {
				setupDocument(document);
			}
			return document;
		}
	
		return null;
	}
	
	/**
	 * Set up the document - default implementation does nothing.
	 * 
	 * @param document the new document
	 */
	protected void setupDocument(IDocument document) 
	{
		// --------------- Setup the partitioner --------------------------
		IDocumentPartitioner partitioner =
				new BNGAPartitioner(
					new BNGAPartitionScanner(),
					new String[] {
						BNGAPartitionScanner.BNGA_COMMENT,
						BNGAPartitionScanner.BNGA_INIT_BLOCK,
						BNGAPartitionScanner.BNGA_REPEAT_BLOCK,
						});
		
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
	}
	
	/**
	 * Tries to read the file pointed at by <code>input</code> if it is an
	 * <code>IPathEditorInput</code>. If the file does not exist, <code>true</code>
	 * is returned.
	 *  
	 * @param document the document to fill with the contents of <code>input</code>
	 * @param input the editor input
	 * @return <code>true</code> if setting the content was successful or no file exists, <code>false</code> otherwise
	 * @throws CoreException if reading the file fails
	 */
	private boolean setDocumentContent(IDocument document, IEditorInput input) throws CoreException {
		// XXX handle encoding
		Reader reader;
		try {
			if (input instanceof IPathEditorInput)
				reader= new FileReader(((IPathEditorInput)input).getPath().toFile());
			else
				return false;
		} catch (FileNotFoundException e) {
			// return empty document and save later
			return true;
		}
		
		try {
			setDocumentContent(document, reader);
			return true;
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, "rulebender", IStatus.OK, "error reading file", e)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Reads in document content from a reader and fills <code>document</code>
	 * 
	 * @param document the document to fill
	 * @param reader the source
	 * @throws IOException if reading fails
	 */
	private void setDocumentContent(IDocument document, Reader reader) throws IOException 
	{
		Reader in= new BufferedReader(reader);
		try {
			
			StringBuffer buffer= new StringBuffer(512);
			char[] readBuffer= new char[512];
			int n= in.read(readBuffer);
			while (n > 0) {
				buffer.append(readBuffer, 0, n);
				n= in.read(readBuffer);
			}
			
			document.set(buffer.toString());
		
		} finally {
			in.close();
		}
	}
	
	
	//TODO
	@Override
	protected IAnnotationModel createAnnotationModel(Object element) throws CoreException 
	{
		
		IFile file = ((FileEditorInput) element).getFile();
		
		//return null;
		ResourceMarkerAnnotationModel annotationModel = new ResourceMarkerAnnotationModel(file);
		return annotationModel;
		
	}
	
	/*
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#doSaveDocument(org.eclipse.core.runtime.IProgressMonitor, java.lang.Object, org.eclipse.jface.text.IDocument, boolean)
	 */
	@Override
    protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) throws CoreException {
		if (element instanceof IPathEditorInput) {
			IPathEditorInput pei= (IPathEditorInput) element;
			IPath path= pei.getPath();
			File file= path.toFile();
			
			try {
				file.createNewFile();

				if (file.exists()) {
					if (file.canWrite()) {
						Writer writer= new FileWriter(file);
						writeDocumentContent(document, writer, monitor);
					} else {
						// XXX prompt to SaveAs
						throw new CoreException(new Status(IStatus.ERROR, "rulebender", IStatus.OK, "file is read-only", null)); //$NON-NLS-1$ //$NON-NLS-2$
					}
				} else {
					throw new CoreException(new Status(IStatus.ERROR, "rulebender", IStatus.OK, "error creating file", null)); //$NON-NLS-1$ //$NON-NLS-2$
				}
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, "rulebender", IStatus.OK, "error when saving file", e)); //$NON-NLS-1$ //$NON-NLS-2$
			}

		}
		
		//System.out.println("WorkSpace Root???????????:" + ResourcesPlugin.getWorkspace().getRoot().getFile(path));
	}
	
	/**
	 * Saves the document contents to a stream.
	 * 
	 * @param document the document to save
	 * @param writer the stream to save it to
	 * @param monitor a progress monitor to report progress
	 * @throws IOException if writing fails
	 */
	private void writeDocumentContent(IDocument document, Writer writer, IProgressMonitor monitor) throws IOException {
		Writer out= new BufferedWriter(writer);
		try {
			out.write(document.get());
		} finally {
			out.close();
		}
	}

	//TODO
	@Override
	protected IRunnableContext getOperationRunner(IProgressMonitor monitor) 
	{
		return null;
	}

}
