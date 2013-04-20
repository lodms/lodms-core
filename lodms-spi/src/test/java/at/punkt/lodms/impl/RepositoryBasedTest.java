/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.punkt.lodms.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

/**
 *
 * @author akreiser@gmail.com
 */
public class RepositoryBasedTest {
    
    protected static Repository repository;
    
    @BeforeClass
    public static void setUpClass() throws RepositoryException {
        repository = new SailRepository(new MemoryStore());
        repository.initialize();
    }
    
    @AfterClass
    public static void tearDownClass() throws RepositoryException {
        repository.shutDown();
    }
    
    @After
    public void tearDown() throws RepositoryException {
        RepositoryConnection con = repository.getConnection();
        try {
            con.clear();
            con.commit();
        } finally {
            con.close();
        }
    }
}
