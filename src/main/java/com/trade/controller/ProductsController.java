package com.trade.controller;

import com.trade.dto.ProductDTO;
import com.trade.dto.UserDTO;
import com.trade.exception.ServiceException;
import com.trade.model.Product;
import com.trade.model.ShoppingCartItem;
import com.trade.model.User;
import com.trade.model.converter.ProductToDTOConverter;
import com.trade.model.converter.UserToDTOConverter;
import com.trade.service.PaginationService;
import com.trade.service.dao.ProductService;
import com.trade.service.dao.ShoppingCartItemService;
import com.trade.service.dao.UserService;
import com.trade.utils.ErrorHandling;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.trade.utils.ConstantsUtils.NUMBER_OF_PRODUCTS_ON_PAGE;


@Controller
@RequestMapping("/products")
public class ProductsController {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private ProductService productService;

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private ShoppingCartItemService shoppingCartItemService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserToDTOConverter userToDTOConverter;

    @Autowired
    private ProductToDTOConverter productToDTOConverter;

    private static final int FIRST_PAGE = 1;

    @GetMapping
    public ModelAndView getProductsList() {


        return new ModelAndView("redirect:/products/page/" + FIRST_PAGE);
    }

    @GetMapping("/page/{page_number}")
    @SuppressWarnings("Duplicates")
    public ModelAndView getProductsByPage(@PathVariable("page_number") Integer pageNumber,
                                          @CookieValue("userID") long userID) {

        try {

            if (pageNumber <= 0) {
                return new ModelAndView("redirect:/products/page/" + FIRST_PAGE);
            }

            List<Product> productsOnPage = productService.findByPage(pageNumber);
            List<ProductDTO> productDTOsOnPageList = productToDTOConverter.convert(productsOnPage);

            Map<Long, UserDTO> idAndUserDTOMap = new HashMap<>();
            for (ProductDTO productDTO : productDTOsOnPageList) {
                User user = userService.findById(productDTO.getSeller());
                UserDTO userDTO = userToDTOConverter.convert(user);
                idAndUserDTOMap.put(userDTO.getId(), userDTO);
            }

            final int totalProductsNumber = productService.findTotalProductsNumber();
            final int numberOfPages = (int) Math.ceil(totalProductsNumber / (NUMBER_OF_PRODUCTS_ON_PAGE * 1.0));

            if (pageNumber > numberOfPages) {
                return new ModelAndView("redirect:/products/page/" + FIRST_PAGE);
            }

            List<Integer> pageNumbers =
                    paginationService.calcPageNumbersForPagination(pageNumber, numberOfPages);

            List<ShoppingCartItem> shoppingCartItems = shoppingCartItemService.findAllById(userID);

            // k - product id, v - number of products in shopping cart
            Map<Long, Long> productsInShoppingCartMap = new HashMap<>();

            // find products from the page that are added to the shopping cart item
            if (null != shoppingCartItems){

                for (ShoppingCartItem cartItem : shoppingCartItems) {

                    for (Product product : productsOnPage) {

                        if (product.getId() == cartItem.getProductId()){

                            productsInShoppingCartMap.put(cartItem.getProductId(), cartItem.getQuantity());
                        }
                    }

                }
            }

            ModelAndView modelAndView = new ModelAndView("products");
            modelAndView.addObject("products", productDTOsOnPageList);
            modelAndView.addObject("productsInShoppingCartMap", productsInShoppingCartMap);
            modelAndView.addObject("number_of_pages", numberOfPages);
            modelAndView.addObject("current_page", pageNumber);
            modelAndView.addObject("userDTOMap", idAndUserDTOMap);

            modelAndView.addObject("page_numbers", pageNumbers);

            return modelAndView;

        } catch (ServiceException e) {

            logger.error("not managed to read products from table by page", e);

            return ErrorHandling.getErrorPage("not managed to get products by page");
        }

    }

    @GetMapping("/{id}")
    public ModelAndView getProductPage(@PathVariable("id") long id,
                                       @CookieValue("userID") long userID) {

        try {

            Product product = productService.findById(id);
            User seller = userService.findById(product.getSeller());
            UserDTO userDTO = userToDTOConverter.convert(seller);

            ModelAndView modelAndView = new ModelAndView("product");
            modelAndView.addObject("product", product);
            modelAndView.addObject("user_dto", userDTO);

            return modelAndView;

        } catch (ServiceException e) {

            logger.error("Error while reading product with id =" + id, e);

            return ErrorHandling.getErrorPage("Error while reading product with id = " + id);
        }

    }


    @GetMapping("/picture/{product_id}")
    public void getProductPicture(@PathVariable("product_id") long productId,
                                  HttpServletResponse response) {

        try {
            logger.info("getting product picture of the user_id = " + productId);

            Product product = productService.findById(productId);

            if (product.getImage() != null) {

                logger.info("user has a picture");

                try {

                    InputStream binaryStream = product.getImage().getBinaryStream();
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                    int nRead;
                    byte[] data = new byte[64];

                    while ((nRead = binaryStream.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    byte[] pictureAsBytes = buffer.toByteArray();

                    response.setContentType("image/jpeg");
                    response.setContentLength(pictureAsBytes.length);
                    response.getOutputStream().write(pictureAsBytes);

                    logger.info("picture is sent with success");

                } catch (IOException e) {
                    logger.info("error while converting Blob image to array of bytes");
                    e.printStackTrace();
                }

            }

        } catch (SQLException | ServiceException e) {
            logger.error("not managed to find product with id = " + productId, e);

            ErrorHandling.getErrorPage("not managed to find product");
        }

    }

}
