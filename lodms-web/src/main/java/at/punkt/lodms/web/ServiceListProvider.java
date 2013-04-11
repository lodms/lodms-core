/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web;

import at.punkt.lodms.spi.extract.Extractor;
import at.punkt.lodms.spi.load.Loader;
import at.punkt.lodms.spi.transform.Transformer;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Alex Kreiser
 */
@Component
public class ServiceListProvider {

    @Autowired(required = true)
    @Resource(name = "extractors")
    private List<Extractor> extractors;

    @Autowired(required = true)
    @Resource(name = "transformers")
    private List<Transformer> transformers;

    @Autowired(required = true)
    @Resource(name = "loaders")
    private List<Loader> loaders;

    public List<Extractor> getAvailableExtractors() {
        return extractors;
    }

    public List<Transformer> getAvailableTransformers() {
        return transformers;
    }

    public List<Loader> getAvailableLoaders() {
        return loaders;
    }
}