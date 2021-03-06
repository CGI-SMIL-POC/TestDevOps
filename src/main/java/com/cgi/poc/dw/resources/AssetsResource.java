package com.cgi.poc.dw.resources;

import com.cgi.poc.dw.core.Asset;
import com.cgi.poc.dw.core.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.cgi.poc.dw.db.AssetDAO;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to serve assets data to users.
 *
 */
@Path("/assets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssetsResource {

    /**
     * Error message return in the case if PUT request body can not be parsed.
     */
    public static final String WRONG_BODY_DATA_FORMAT
            = "Wrong body data format";
    /**
     * Logger.
     */
    private static final Logger LOGGER
            = LoggerFactory.getLogger(AssetsResource.class);
    /**
     * DAO to manipulate assets.
     */
    private final AssetDAO assetDAO;

    /**
     * Constructor to initialize DAO.
     *
     * @param assetDAO DAO to manipulate assets.
     */
    public AssetsResource(final AssetDAO assetDAO) {
        this.assetDAO = assetDAO;
    }

    /**
     * Method returns all assets stored by a particular user.
     *
     * @param user Authenticated user with whose assets we work.
     * @return list of assets stored by a particular user.
     */
    @GET
    @UnitOfWork
    public List<Asset> getAssets(@Auth User user) {
        return assetDAO.findByUserId(user.getId());
    }

    /**
     * Method returns single asset data.
     *
     * @param id the id of a asset.
     * @param user Authenticated user with whose assets we work.
     * @return Optional containing a asset or empty Optional if the asset
     * was not found.
     */
    @GET
    @Path("/{id}")
    @UnitOfWork
    public Optional<Asset> getAsset(@PathParam("id") IntParam id,
                                    @Auth User user) {
        return assetDAO.findByIdAndUserId(id.get(), user.getId());
    }

    /**
     * Method to add new assets.
     *
     * @param asset A asset to add
     * @param user Authenticated user with whose assets we work.
     * @return The saved asset containing the id generated by the database.
     */
    @POST
    @UnitOfWork
    public Asset addAsset(@Valid @NotNull Asset asset,
                          @Auth User user) {

        asset.setUser(user);
        return assetDAO.save(asset);
    }

    /**
     * A method to modify an existing asset data.
     *
     * @param id the id of the asset to be modified.
     * @param jsonData Modifications in JSON format.
     * @param user Authenticated user with whose assets we work.
     * @return Asset with modified fields or throws an exception if asset
     * was not found.
     */
    @PUT
    @Path("/{id}")
    @UnitOfWork
    public Asset modifyAsset(@PathParam("id") IntParam id,
                             String jsonData,
                             @Auth User user) {

        Asset asset = findAssetOrTrowException(id, user);

        // Update asset data
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> changeMap = null;
        try {
            changeMap = objectMapper.readValue(jsonData, HashMap.class);
            purgeMap(changeMap);
            BeanUtils.populate(asset, changeMap);
            return assetDAO.save(asset);
        } catch (IOException |
                IllegalAccessException |
                InvocationTargetException ex) {
            LOGGER.warn(WRONG_BODY_DATA_FORMAT, ex);
            throw new WebApplicationException(WRONG_BODY_DATA_FORMAT,
                    ex,
                    Response.Status.BAD_REQUEST);
        } finally {
            if (changeMap != null) {
                changeMap.clear();
            }
        }
    }

    /**
     * A method to remove assets.
     *
     * @param id the id of a asset to be deleted.
     * @param user Authenticated user with whose assets we work.
     * @return Removed asset data or throws an exception if the asset with
     * the id provided was not found.
     */
    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Asset deleteAsset(@PathParam("id") IntParam id,
                             @Auth User user) {
        Asset asset
                = findAssetOrTrowException(id, user);
        assetDAO.delete(id.get());
        return asset;
    }

    /**
     * A method to remove null and empty values from the change map. Necessary
     * if not fields in the changed object are filled.
     *
     * @param changeMap map of object field values.
     */
    protected void purgeMap(final Map<String, String> changeMap) {
        changeMap.remove("id");
        changeMap.entrySet().removeIf(
                entry -> Strings.isNullOrEmpty(entry.getValue())
        );
    }

    /**
     * Method looks for a asset by id and User id and returns the asset or
     * throws NotFoundException otherwise.
     *
     * @param id the id of a asset.
     * @param user the id of the owner.
     * @return Asset
     */
    private Asset findAssetOrTrowException(IntParam id,
                                           @Auth User user) {
        Asset asset = assetDAO.findByIdAndUserId(
                id.get(), user.getId()
        ).orElseThrow(()
                -> new NotFoundException("Asset requested was not found."));
        return asset;
    }

}
