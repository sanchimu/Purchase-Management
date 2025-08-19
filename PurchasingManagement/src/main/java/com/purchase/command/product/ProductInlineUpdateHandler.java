package com.purchase.command.product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.LinkedHashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdbc.connection.ConnectionProvider;
import mvc.command.CommandHandler;

public class ProductInlineUpdateHandler implements CommandHandler {

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            return redirectBack(req, res);
        }

        String[] ids = req.getParameterValues("ids");
        if (ids == null || ids.length == 0) {
            return redirectBack(req, res);
        }

        try (Connection conn = ConnectionProvider.getConnection()) {
            conn.setAutoCommit(false);

            String sqlNameOnly = "UPDATE product SET product_name = ? WHERE product_id = ?";
            String sqlPriceOnly = "UPDATE product SET price = ? WHERE product_id = ?";
            String sqlBoth = "UPDATE product SET product_name = ?, price = ? WHERE product_id = ?";

            try (PreparedStatement psName = conn.prepareStatement(sqlNameOnly);
                 PreparedStatement psPrice = conn.prepareStatement(sqlPriceOnly);
                 PreparedStatement psBoth = conn.prepareStatement(sqlBoth)) {

                for (String id : new LinkedHashSet<>(Arrays.asList(ids))) {
                    if (id == null || id.isBlank()) continue;

                    String name = req.getParameter("name[" + id + "]");
                    String priceStr = req.getParameter("price[" + id + "]");

                    boolean hasName = name != null && !name.trim().isEmpty();
                    boolean hasPrice = false;
                    Integer price = null;

                    if (priceStr != null && !priceStr.trim().isEmpty()) {
                        String digits = priceStr.replaceAll("[^0-9]", "");
                        if (!digits.isEmpty()) {
                            try {
                                price = Integer.valueOf(digits);
                                hasPrice = true;
                            } catch (NumberFormatException ignored) {}
                        }
                    }

                    if (!hasName && !hasPrice) continue;

                    if (hasName && hasPrice) {
                        psBoth.setString(1, name.trim());
                        psBoth.setInt(2, price);
                        psBoth.setString(3, id);
                        psBoth.addBatch();
                    } else if (hasName) {
                        psName.setString(1, name.trim());
                        psName.setString(2, id);
                        psName.addBatch();
                    } else { // hasPrice only
                        psPrice.setInt(1, price);
                        psPrice.setString(2, id);
                        psPrice.addBatch();
                    }
                }

                psBoth.executeBatch();
                psName.executeBatch();
                psPrice.executeBatch();
            }

            conn.commit();
        } catch (Exception e) {
            // 실패 시에도 목록으로 복귀 (실무라면 로깅)
        }

        return redirectBack(req, res);
    }

    private String redirectBack(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String referer = req.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            res.sendRedirect(referer);
        } else {
            String ih = req.getParameter("includeHidden");
            String qs = ("1".equals(ih) || "true".equalsIgnoreCase(ih)) ? "?includeHidden=1" : "";
            res.sendRedirect(req.getContextPath() + "/listProducts.do" + qs);
        }
        return null;
    }
}
